import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';

const prisma = new PrismaClient();

export interface JwtPayload {
  userId: string;
  username: string;
  role: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export class AuthService {
  private readonly SALT_ROUNDS = 12;
  private readonly JWT_SECRET = process.env.JWT_SECRET!;
  private readonly JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '7d';
  private readonly JWT_REFRESH_SECRET = process.env.JWT_REFRESH_SECRET!;
  private readonly JWT_REFRESH_EXPIRES_IN = process.env.JWT_REFRESH_EXPIRES_IN || '30d';

  async hashPassword(password: string): Promise<string> {
    return bcrypt.hash(password, this.SALT_ROUNDS);
  }

  async comparePassword(password: string, hash: string): Promise<boolean> {
    return bcrypt.compare(password, hash);
  }

  generateAccessToken(payload: JwtPayload): string {
    return jwt.sign(payload, this.JWT_SECRET, {
      expiresIn: this.JWT_EXPIRES_IN,
      issuer: 'your-app-name',
      audience: 'your-app-users',
    });
  }

  generateRefreshToken(payload: JwtPayload): string {
    return jwt.sign(payload, this.JWT_REFRESH_SECRET, {
      expiresIn: this.JWT_REFRESH_EXPIRES_IN,
      issuer: 'your-app-name',
      audience: 'your-app-users',
    });
  }

  generateTokens(user: { id: bigint; username: string; role: string }): AuthTokens {
    const payload: JwtPayload = {
      userId: user.id.toString(),
      username: user.username,
      role: user.role,
    };

    return {
      accessToken: this.generateAccessToken(payload),
      refreshToken: this.generateRefreshToken(payload),
    };
  }

  verifyAccessToken(token: string): JwtPayload {
    try {
      return jwt.verify(token, this.JWT_SECRET, {
        issuer: 'your-app-name',
        audience: 'your-app-users',
      }) as JwtPayload;
    } catch (error) {
      throw new Error('Invalid or expired access token');
    }
  }

  verifyRefreshToken(token: string): JwtPayload {
    try {
      return jwt.verify(token, this.JWT_REFRESH_SECRET, {
        issuer: 'your-app-name',
        audience: 'your-app-users',
      }) as JwtPayload;
    } catch (error) {
      throw new Error('Invalid or expired refresh token');
    }
  }

  async login(username: string, password: string): Promise<AuthTokens | null> {
    const user = await prisma.user.findUnique({
      where: { username },
    });

    if (!user || !user.enabled) {
      return null;
    }

    const isValid = await this.comparePassword(password, user.password);
    if (!isValid) {
      return null;
    }

    return this.generateTokens(user);
  }

  async register(data: {
    username: string;
    password: string;
    firstName: string;
    lastName: string;
    role?: string;
  }): Promise<AuthTokens> {
    const hashedPassword = await this.hashPassword(data.password);

    const user = await prisma.user.create({
      data: {
        username: data.username,
        password: hashedPassword,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role || 'USER',
        enabled: true,
      },
    });

    // Create default user preferences
    await prisma.userPreferences.create({
      data: {
        userId: user.id,
        temperatureUnit: 'CELSIUS',
      },
    });

    return this.generateTokens(user);
  }

  async refreshAccessToken(refreshToken: string): Promise<string | null> {
    try {
      const payload = this.verifyRefreshToken(refreshToken);
      
      const user = await prisma.user.findUnique({
        where: { id: BigInt(payload.userId) },
      });

      if (!user || !user.enabled) {
        return null;
      }

      return this.generateAccessToken({
        userId: user.id.toString(),
        username: user.username,
        role: user.role,
      });
    } catch (error) {
      return null;
    }
  }
}

export const authService = new AuthService();