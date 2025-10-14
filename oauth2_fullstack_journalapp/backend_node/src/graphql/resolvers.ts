// src/graphql/resolvers.ts
import { PrismaClient } from '@prisma/client';
import { authService } from '../services/authService';
import { GraphQLError } from 'graphql';

const prisma = new PrismaClient();

interface Context {
  user?: { userId: string; username: string; role: string };
}

const requireAuth = (context: Context) => {
  if (!context.user) {
    throw new GraphQLError('Authentication required', {
      extensions: { code: 'UNAUTHENTICATED' },
    });
  }
  return context.user;
};

const calculateSeason = (date: Date): string => {
  const month = date.getMonth() + 1;
  if (month >= 3 && month <= 5) return 'SPRING';
  if (month >= 6 && month <= 8) return 'SUMMER';
  if (month >= 9 && month <= 11) return 'FALL';
  return 'WINTER';
};

export const resolvers = {
  Query: {
    me: async (_: any, __: any, context: Context) => {
      const user = requireAuth(context);
      return prisma.user.findUnique({
        where: { id: BigInt(user.userId) },
        include: { userPreferences: true },
      });
    },

    journalEntry: async (_: any, { id }: { id: string }, context: Context) => {
      const user = requireAuth(context);
      const entry = await prisma.journalEntry.findUnique({
        where: { id: BigInt(id) },
        include: { medications: true },
      });

      if (!entry || entry.userId.toString() !== user.userId) {
        throw new GraphQLError('Journal entry not found', {
          extensions: { code: 'NOT_FOUND' },
        });
      }

      return entry;
    },

    journalEntries: async (
      _: any,
      { limit = 50, offset = 0 }: { limit?: number; offset?: number },
      context: Context
    ) => {
      const user = requireAuth(context);
      return prisma.journalEntry.findMany({
        where: { userId: BigInt(user.userId) },
        include: { medications: true },
        orderBy: { entryDate: 'desc' },
        take: limit,
        skip: offset,
      });
    },

    journalEntriesByDateRange: async (
      _: any,
      { startDate, endDate }: { startDate: string; endDate: string },
      context: Context
    ) => {
      const user = requireAuth(context);
      return prisma.journalEntry.findMany({
        where: {
          userId: BigInt(user.userId),
          entryDate: {
            gte: new Date(startDate),
            lte: new Date(endDate),
          },
        },
        include: { medications: true },
        orderBy: { entryDate: 'desc' },
      });
    },
  },

  Mutation: {
    register: async (_: any, { input }: any) => {
      const tokens = await authService.register(input);
      const user = await prisma.user.findUnique({
        where: { username: input.username },
        include: { userPreferences: true },
      });
      return { ...tokens, user };
    },

    login: async (_: any, { input }: any) => {
      const tokens = await authService.login(input.username, input.password);
      if (!tokens) {
        throw new GraphQLError('Invalid credentials', {
          extensions: { code: 'UNAUTHORIZED' },
        });
      }
      const user = await prisma.user.findUnique({
        where: { username: input.username },
        include: { userPreferences: true },
      });
      return { ...tokens, user };
    },

    refreshToken: async (_: any, { refreshToken }: { refreshToken: string }) => {
      const newAccessToken = await authService.refreshAccessToken(refreshToken);
      if (!newAccessToken) {
        throw new GraphQLError('Invalid refresh token', {
          extensions: { code: 'UNAUTHORIZED' },
        });
      }
      return newAccessToken;
    },

    createJournalEntry: async (_: any, { input }: any, context: Context) => {
      const user = requireAuth(context);
      const entryDate = new Date(input.entryDate);
      const season = calculateSeason(entryDate);

      return prisma.journalEntry.create({
        data: {
          userId: BigInt(user.userId),
          entryDate,
          mood: input.mood,
          weatherCondition: input.weatherCondition,
          temperatureCelsius: input.temperatureCelsius,
          humidity: input.humidity,
          season,
          notes: input.notes,
        },
        include: { medications: true },
      });
    },

    updateJournalEntry: async (
      _: any,
      { id, input }: { id: string; input: any },
      context: Context
    ) => {
      const user = requireAuth(context);
      const entry = await prisma.journalEntry.findUnique({
        where: { id: BigInt(id) },
      });

      if (!entry || entry.userId.toString() !== user.userId) {
        throw new GraphQLError('Journal entry not found', {
          extensions: { code: 'NOT_FOUND' },
        });
      }

      return prisma.journalEntry.update({
        where: { id: BigInt(id) },
        data: input,
        include: { medications: true },
      });
    },

    deleteJournalEntry: async (_: any, { id }: { id: string }, context: Context) => {
      const user = requireAuth(context);
      const entry = await prisma.journalEntry.findUnique({
        where: { id: BigInt(id) },
      });

      if (!entry || entry.userId.toString() !== user.userId) {
        throw new GraphQLError('Journal entry not found', {
          extensions: { code: 'NOT_FOUND' },
        });
      }

      await prisma.journalEntry.delete({ where: { id: BigInt(id) } });
      return true;
    },

    addMedication: async (
      _: any,
      { journalEntryId, input }: { journalEntryId: string; input: any },
      context: Context
    ) => {
      const user = requireAuth(context);
      const entry = await prisma.journalEntry.findUnique({
        where: { id: BigInt(journalEntryId) },
      });

      if (!entry || entry.userId.toString() !== user.userId) {
        throw new GraphQLError('Journal entry not found', {
          extensions: { code: 'NOT_FOUND' },
        });
      }

      return prisma.medication.create({
        data: {
          journalEntryId: BigInt(journalEntryId),
          name: input.name,
          dosage: input.dosage,
          timeTaken: new Date(input.timeTaken),
        },
      });
    },

    removeMedication: async (_: any, { medicationId }: { medicationId: string }, context: Context) => {
      const user = requireAuth(context);
      const medication = await prisma.medication.findUnique({
        where: { id: BigInt(medicationId) },
        include: { journalEntry: true },
      });

      if (!medication || medication.journalEntry.userId.toString() !== user.userId) {
        throw new GraphQLError('Medication not found', {
          extensions: { code: 'NOT_FOUND' },
        });
      }

      await prisma.medication.delete({ where: { id: BigInt(medicationId) } });
      return true;
    },

    updateUserPreferences: async (_: any, { input }: any, context: Context) => {
      const user = requireAuth(context);
      return prisma.userPreferences.update({
        where: { userId: BigInt(user.userId) },
        data: input,
      });
    },
  },

  User: {
    id: (parent: any) => parent.id.toString(),
    journalEntries: (parent: any) =>
      prisma.journalEntry.findMany({
        where: { userId: parent.id },
        include: { medications: true },
      }),
  },

  JournalEntry: {
    id: (parent: any) => parent.id.toString(),
    entryDate: (parent: any) => parent.entryDate.toISOString(),
    createdAt: (parent: any) => parent.createdAt.toISOString(),
    updatedAt: (parent: any) => parent.updatedAt.toISOString(),
    weather: (parent: any) => ({
      condition: parent.weatherCondition,
      temperatureCelsius: parent.temperatureCelsius,
      humidity: parent.humidity,
    }),
  },

  Medication: {
    id: (parent: any) => parent.id.toString(),
    timeTaken: (parent: any) => parent.timeTaken.toISOString(),
  },

  UserPreferences: {
    id: (parent: any) => parent.id.toString(),
    createdAt: (parent: any) => parent.createdAt.toISOString(),
    updatedAt: (parent: any) => parent.updatedAt.toISOString(),
  },
};