// src/graphql/schema.ts
export const typeDefs = `#graphql
  type User {
    id: ID!
    username: String!
    firstName: String!
    lastName: String!
    role: String!
    enabled: Boolean!
    userPreferences: UserPreferences
    journalEntries: [JournalEntry!]!
  }

  type UserPreferences {
    id: ID!
    temperatureUnit: TemperatureUnit!
    createdAt: String!
    updatedAt: String!
  }

  type JournalEntry {
    id: ID!
    entryDate: String!
    mood: Mood!
    weather: WeatherSnapshot
    medications: [Medication!]!
    season: Season
    notes: String
    createdAt: String!
    updatedAt: String!
  }

  type WeatherSnapshot {
    condition: WeatherCondition
    temperatureCelsius: Float
    humidity: Int
  }

  type Medication {
    id: ID!
    name: String!
    dosage: String!
    timeTaken: String!
  }

  type AuthPayload {
    accessToken: String!
    refreshToken: String!
    user: User!
  }

  enum Mood {
    POOR
    FAIR
    NEUTRAL
    GOOD
    GREAT
  }

  enum Season {
    SPRING
    SUMMER
    FALL
    WINTER
  }

  enum TemperatureUnit {
    CELSIUS
    FAHRENHEIT
  }

  enum WeatherCondition {
    SUNNY
    RAINY
    SNOW
    CLOUDY
    OVERCAST
    STORM
  }

  input RegisterInput {
    username: String!
    password: String!
    firstName: String!
    lastName: String!
  }

  input LoginInput {
    username: String!
    password: String!
  }

  input CreateJournalEntryInput {
    entryDate: String!
    mood: Mood!
    weatherCondition: WeatherCondition
    temperatureCelsius: Float
    humidity: Int
    notes: String
  }

  input UpdateJournalEntryInput {
    mood: Mood
    weatherCondition: WeatherCondition
    temperatureCelsius: Float
    humidity: Int
    notes: String
  }

  input CreateMedicationInput {
    name: String!
    dosage: String!
    timeTaken: String!
  }

  input UpdateUserPreferencesInput {
    temperatureUnit: TemperatureUnit
  }

  type Query {
    me: User
    journalEntry(id: ID!): JournalEntry
    journalEntries(limit: Int, offset: Int): [JournalEntry!]!
    journalEntriesByDateRange(startDate: String!, endDate: String!): [JournalEntry!]!
  }

  type Mutation {
    register(input: RegisterInput!): AuthPayload!
    login(input: LoginInput!): AuthPayload!
    refreshToken(refreshToken: String!): String!
    
    createJournalEntry(input: CreateJournalEntryInput!): JournalEntry!
    updateJournalEntry(id: ID!, input: UpdateJournalEntryInput!): JournalEntry!
    deleteJournalEntry(id: ID!): Boolean!
    
    addMedication(journalEntryId: ID!, input: CreateMedicationInput!): Medication!
    removeMedication(medicationId: ID!): Boolean!
    
    updateUserPreferences(input: UpdateUserPreferencesInput!): UserPreferences!
  }
`;