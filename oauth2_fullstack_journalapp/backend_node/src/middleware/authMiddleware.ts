// src/middleware/authMiddleware.ts
import { Request, Response, NextFunction } from 'express';
import { authService, JwtPayload } from '../services/authService';

export interface AuthRequest extends Request {
  user?: JwtPayload;
}

export const authenticate = (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    // Check for token in Authorization header
    const authHeader = req.headers.authorization;
    let token: string | undefined;

    if (authHeader && authHeader.startsWith('Bearer ')) {
      token = authHeader.substring(7);
    } else {
      // Fallback to HTTP-only cookie
      token = req.cookies?.accessToken;
    }

    if (!token) {
      return res.status(401).json({ error: 'Authentication required' });
    }

    const payload = authService.verifyAccessToken(token);
    req.user = payload;
    next();
  } catch (error) {
    return res.status(401).json({ error: 'Invalid or expired token' });
  }
};

export const authorize = (...roles: string[]) => {
  return (req: AuthRequest, res: Response, next: NextFunction) => {
    if (!req.user) {
      return res.status(401).json({ error: 'Authentication required' });
    }

    if (!roles.includes(req.user.role)) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }

    next();
  };
};

// GraphQL Context Authentication
export const getAuthenticatedUser = (req: AuthRequest): JwtPayload | null => {
  try {
    const authHeader = req.headers.authorization;
    let token: string | undefined;

    if (authHeader && authHeader.startsWith('Bearer ')) {
      token = authHeader.substring(7);
    } else {
      token = req.cookies?.accessToken;
    }

    if (!token) {
      return null;
    }

    return authService.verifyAccessToken(token);
  } catch (error) {
    return null;
  }
};