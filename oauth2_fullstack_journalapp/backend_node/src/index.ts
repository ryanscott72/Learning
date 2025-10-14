import fs from 'fs';
import https from 'https';
import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@as-integrations/express5';
import cookieParser from 'cookie-parser';
import cors from 'cors';
import dotenv from 'dotenv';
import express from 'express';
import helmet from 'helmet';
import { resolvers } from './graphql/resolvers';
import { typeDefs } from './graphql/schema';
import type { AuthRequest } from './middleware/authMiddleware';
import { getAuthenticatedUser } from './middleware/authMiddleware';

dotenv.config();

interface GraphQLContext {
  user?: { userId: string; username: string; role: string };
  req: AuthRequest;
  res: express.Response;
}

async function startServer() {
  const app = express();
  const PORT = process.env.PORT || 4000;

  // Security headers
  app.use(
    helmet({
      contentSecurityPolicy: process.env.NODE_ENV === 'production' ? undefined : false,
      crossOriginEmbedderPolicy: false,
    })
  );

  // CORS configuration
  const corsOptions = {
    origin: process.env.CORS_ORIGIN || 'http://localhost:3000',
    credentials: true,
    methods: ['GET', 'POST', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization'],
  };
  app.use(cors(corsOptions));

  // Body parsing and cookies
  app.use(express.json({ limit: '10mb' }));
  app.use(express.urlencoded({ extended: true }));
  app.use(cookieParser());

  // Rate limiting middleware (simple implementation)
  const requestCounts = new Map<string, { count: number; resetTime: number }>();
  const RATE_LIMIT = 100;
  const RATE_WINDOW = 15 * 60 * 1000; // 15 minutes

  app.use((req, res, next) => {
    const ip = req.ip || 'unknown';
    const now = Date.now();
    const userLimit = requestCounts.get(ip);

    if (!userLimit || now > userLimit.resetTime) {
      requestCounts.set(ip, { count: 1, resetTime: now + RATE_WINDOW });
      return next();
    }

    if (userLimit.count >= RATE_LIMIT) {
      return res.status(429).json({ error: 'Too many requests' });
    }

    userLimit.count++;
    next();
  });

  // Health check endpoint
  app.get('/health', (req, res) => {
    res.json({ status: 'ok', timestamp: new Date().toISOString() });
  });

  // Initialize Apollo Server
  const apolloServer = new ApolloServer<GraphQLContext>({
    typeDefs,
    resolvers,
    introspection: process.env.NODE_ENV !== 'production',
    formatError: (error) => {
      // Log error for monitoring
      console.error('GraphQL Error:', error);

      // Don't expose internal server errors in production
      if (process.env.NODE_ENV === 'production') {
        if (error.extensions?.code === 'INTERNAL_SERVER_ERROR') {
          return {
            message: 'An internal error occurred',
            extensions: { code: 'INTERNAL_SERVER_ERROR' },
          };
        }
      }

      return error;
    },
  });

  await apolloServer.start();

  // GraphQL endpoint with authentication context
  app.use(
    '/graphql',
    expressMiddleware(apolloServer, {
      context: async ({ req, res }): Promise<GraphQLContext> => {
        const user = getAuthenticatedUser(req as AuthRequest);
        return { user: user || undefined, req: req as AuthRequest, res };
      },
    })
  );

  // REST endpoints for authentication (optional - can also use GraphQL)
  app.post('/auth/login', async (req, res) => {
    try {
      const { username, password } = req.body;

      if (!username || !password) {
        return res.status(400).json({ error: 'Username and password required' });
      }

      const { authService } = await import('./services/authService');
      const tokens = await authService.login(username, password);

      if (!tokens) {
        return res.status(401).json({ error: 'Invalid credentials' });
      }

      // Set HTTP-only cookies
      res.cookie('accessToken', tokens.accessToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        maxAge: 7 * 24 * 60 * 60 * 1000, // 7 days
      });

      res.cookie('refreshToken', tokens.refreshToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        maxAge: 30 * 24 * 60 * 60 * 1000, // 30 days
      });

      res.json({ message: 'Login successful', tokens });
    } catch (error) {
      console.error('Login error:', error);
      res.status(500).json({ error: 'Internal server error' });
    }
  });

  app.post('/auth/logout', (req, res) => {
    res.clearCookie('accessToken');
    res.clearCookie('refreshToken');
    res.json({ message: 'Logout successful' });
  });

  app.post('/auth/refresh', async (req, res) => {
    try {
      const refreshToken = req.cookies?.refreshToken || req.body.refreshToken;

      if (!refreshToken) {
        return res.status(401).json({ error: 'Refresh token required' });
      }

      const { authService } = await import('./services/authService');
      const newAccessToken = await authService.refreshAccessToken(refreshToken);

      if (!newAccessToken) {
        return res.status(401).json({ error: 'Invalid refresh token' });
      }

      res.cookie('accessToken', newAccessToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        maxAge: 7 * 24 * 60 * 60 * 1000,
      });

      res.json({ accessToken: newAccessToken });
    } catch (error) {
      console.error('Refresh token error:', error);
      res.status(500).json({ error: 'Internal server error' });
    }
  });

  // 404 handler
  app.use((req, res) => {
    res.status(404).json({ error: 'Route not found' });
  });

  // Global error handler
  app.use((err: Error, req: express.Request, res: express.Response, next: express.NextFunction) => {
    console.error('Server error:', err);
    res.status(500).json({ error: 'Internal server error' });
  });

  // Start server (HTTP for development, HTTPS for production)
  if (
    process.env.NODE_ENV === 'production' &&
    process.env.SSL_KEY_PATH &&
    process.env.SSL_CERT_PATH
  ) {
    const httpsOptions = {
      key: fs.readFileSync(process.env.SSL_KEY_PATH),
      cert: fs.readFileSync(process.env.SSL_CERT_PATH),
    };

    https.createServer(httpsOptions, app).listen(PORT, () => {
      console.log(`🚀 HTTPS Server running on https://localhost:${PORT}`);
      console.log(`🔒 GraphQL endpoint: https://localhost:${PORT}/graphql`);
    });
  } else {
    app.listen(PORT, () => {
      console.log(`🚀 Server running on http://localhost:${PORT}`);
      console.log(`📊 GraphQL endpoint: http://localhost:${PORT}/graphql`);
      console.log(`⚠️  Running in ${process.env.NODE_ENV || 'development'} mode`);
    });
  }
}

startServer().catch((error) => {
  console.error('Failed to start server:', error);
  process.exit(1);
});
