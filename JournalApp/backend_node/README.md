# Node.js + TypeScript Secure Backend Setup Guide

## 🎉 Specific Versions Used

This project uses the following exact versions:

| Package                            | Version | Category              |
| ---------------------------------- | ------- | --------------------- |
| `@apollo/server`                   | 5.0.0   | GraphQL Server        |
| `@as-integrations/express5`        | 1.1.0   | Express Integration   |
| `@prisma/client`                   | 6.17.1  | ORM Client            |
| `prisma`                           | 6.17.1  | ORM CLI               |
| `bcrypt`                           | 6.0.0   | Password Hashing      |
| `express`                          | 5.1.0   | Web Framework         |
| `graphql`                          | 16.11.0 | GraphQL Core          |
| `helmet`                           | 8.1.0   | Security Headers      |
| `dotenv`                           | 17.2.3  | Environment Variables |
| `typescript`                       | 5.9.3   | TypeScript Compiler   |
| `eslint`                           | 9.37.0  | Linting               |
| `@typescript-eslint/eslint-plugin` | 8.46.1  | TypeScript Linting    |
| `@typescript-eslint/parser`        | 8.46.1  | TypeScript Parser     |
| `eslint-config-prettier`           | 10.1.8  | Prettier Integration  |
| `eslint-plugin-import`             | 2.32.0  | Import Linting        |
| `eslint-plugin-n`                  | 17.23.1 | Node.js Linting       |
| `prettier`                         | 3.6.2   | Code Formatter        |
| `tsx`                              | 4.20.6  | TypeScript Runner     |

**Node.js Requirement:** v20 or later (v24 recommended)

## Project Structure

```
project-root/
├── prisma/
│   └── schema.prisma
├── src/
│   ├── graphql/
│   │   ├── schema.ts
│   │   └── resolvers.ts
│   ├── middleware/
│   │   └── authMiddleware.ts
│   ├── services/
│   │   └── authService.ts
│   └── index.ts
├── .env
├── .env.example
├── package.json
└── tsconfig.json
```

## Installation Steps

### 1. Prerequisites

Ensure you have **Node.js 20 or later** installed (required for Apollo Server 5):

```bash
node --version  # Should be v20.0.0 or higher
```

### 2. Initialize Project

```bash
npm init -y
npm install
```

### 3. Setup Code Quality Tools

```bash
# ESLint and Prettier are already configured
# Run linting to verify setup
npm run lint
npm run format
```

### 4. Setup PostgreSQL Database

Create a PostgreSQL database and update the `.env` file with your connection string:

```bash
cp .env.example .env
# Edit .env with your database credentials
```

### 5. Initialize Prisma

```bash
npx prisma generate
npx prisma migrate dev --name init
```

### 4. Start Development Server

```bash
npm run dev
```

### 5. Code Quality Commands

```bash
# Check for linting errors
npm run lint

# Auto-fix linting errors
npm run lint:fix

# Format code with Prettier
npm run format

# Type check without building
npm run type-check
```

## Security Features Implemented

### ✅ Authentication & Authorization

- **JWT tokens** with access and refresh token support
- **BCrypt password hashing** (12 salt rounds)
- **HTTP-only cookies** for token storage
- Role-based access control (RBAC)

### ✅ API Security

- **GraphQL API** with authentication context
- **HTTPS/TLS** support for production
- **CORS** configuration with credentials
- **Helmet.js** security headers
- Rate limiting (100 requests per 15 minutes)

### ✅ Database Security

- **Prisma ORM** - automatic SQL injection protection via parameterized queries
- **Input validation** through GraphQL schema
- **Row-level security** - users can only access their own data

## API Usage

### REST Authentication Endpoints

#### Register (via GraphQL)

```graphql
mutation {
  register(
    input: { username: "john_doe", password: "SecurePass123!", firstName: "John", lastName: "Doe" }
  ) {
    accessToken
    refreshToken
    user {
      id
      username
      firstName
      lastName
    }
  }
}
```

#### Login (REST)

```bash
curl -X POST http://localhost:4000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "SecurePass123!"}'
```

#### Login (GraphQL)

```graphql
mutation {
  login(input: { username: "john_doe", password: "SecurePass123!" }) {
    accessToken
    refreshToken
    user {
      id
      username
    }
  }
}
```

### Protected GraphQL Queries

#### Get Current User

```graphql
query {
  me {
    id
    username
    firstName
    lastName
    userPreferences {
      temperatureUnit
    }
  }
}
```

#### Create Journal Entry

```graphql
mutation {
  createJournalEntry(
    input: {
      entryDate: "2025-10-14"
      mood: GOOD
      weatherCondition: SUNNY
      temperatureCelsius: 22.5
      humidity: 65
      notes: "Had a great day today!"
    }
  ) {
    id
    entryDate
    mood
    weather {
      condition
      temperatureCelsius
    }
    season
  }
}
```

#### Get Journal Entries

```graphql
query {
  journalEntries(limit: 10, offset: 0) {
    id
    entryDate
    mood
    weather {
      condition
      temperatureCelsius
    }
    medications {
      id
      name
      dosage
      timeTaken
    }
    notes
  }
}
```

#### Add Medication to Entry

```graphql
mutation {
  addMedication(
    journalEntryId: "1"
    input: { name: "Aspirin", dosage: "500mg", timeTaken: "2025-10-14T08:00:00Z" }
  ) {
    id
    name
    dosage
    timeTaken
  }
}
```

## Authentication Methods

### Method 1: Bearer Token (Header)

```bash
curl -X POST http://localhost:4000/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{"query": "{ me { username } }"}'
```

### Method 2: HTTP-Only Cookies

Cookies are automatically sent with requests after login. No additional headers needed.

## Token Refresh

```bash
curl -X POST http://localhost:4000/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "YOUR_REFRESH_TOKEN"}'
```

## Production Deployment

### 1. Environment Variables

Update `.env` for production:

```bash
NODE_ENV=production
JWT_SECRET=<strong-random-secret>
JWT_REFRESH_SECRET=<different-strong-secret>
CORS_ORIGIN=https://yourdomain.com
SSL_KEY_PATH=/path/to/private-key.pem
SSL_CERT_PATH=/path/to/certificate.pem
```

### 2. Build and Run

```bash
npm run build
npm start
```

### 3. SSL/TLS Certificate

Generate or obtain SSL certificates:

```bash
# Self-signed (development only)
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

# Production: Use Let's Encrypt
certbot certonly --standalone -d yourdomain.com
```

## Security Best Practices

1. **Never commit `.env` file** to version control
2. **Use strong JWT secrets** (at least 32 characters)
3. **Enable HTTPS in production**
4. **Regularly update dependencies** for security patches
5. **Implement rate limiting** per user/IP
6. **Log authentication attempts** for monitoring
7. **Use environment-specific configurations**
8. **Implement password complexity requirements**
9. **Add refresh token rotation** for enhanced security
10. **Monitor for suspicious activity**

## Comparison with Spring Security

| Feature                  | Spring Security   | This Implementation       |
| ------------------------ | ----------------- | ------------------------- |
| Password Hashing         | BCrypt            | ✅ BCrypt (12 rounds)     |
| JWT Tokens               | Optional          | ✅ Built-in               |
| HTTP-Only Cookies        | Supported         | ✅ Implemented            |
| CORS                     | @CrossOrigin      | ✅ cors middleware        |
| HTTPS/TLS                | SSL Config        | ✅ HTTPS server           |
| SQL Injection Protection | PreparedStatement | ✅ Prisma ORM             |
| Role-Based Access        | @PreAuthorize     | ✅ authorize() middleware |
| Authentication Context   | SecurityContext   | ✅ GraphQL context        |

## Testing

### Test Authentication

```bash
# Register new user
curl -X POST http://localhost:4000/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { register(input: {username: \"test\", password: \"Test123!\", firstName: \"Test\", lastName: \"User\"}) { accessToken } }"}'

# Login
curl -X POST http://localhost:4000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "Test123!"}'
```

## Troubleshooting

### Common Issues

**Database Connection Failed**

- Verify PostgreSQL is running
- Check DATABASE_URL in .env
- Ensure database exists

**JWT Verification Failed**

- Check JWT_SECRET matches between token generation and verification
- Verify token hasn't expired
- Ensure Bearer token format is correct

**CORS Errors**

- Update CORS_ORIGIN in .env
- Verify credentials: true in CORS config

## Additional Resources

- [Prisma Documentation](https://www.prisma.io/docs)
- [Apollo Server Documentation](https://www.apollographql.com/docs/apollo-server/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Security Guidelines](https://owasp.org/)

# VS Code Setup Guide

## 🚀 Quick Setup

### 1. Install Recommended Extensions

When you open this project in VS Code, you'll see a notification to install recommended extensions. Click **Install All** or install them individually:

#### Essential Extensions (Required):

- **ESLint** (`dbaeumer.vscode-eslint`) - Linting support
- **Prettier** (`esbenp.prettier-vscode`) - Code formatting
- **Prisma** (`prisma.prisma`) - Prisma schema support
- **TypeScript** (`ms-vscode.vscode-typescript-next`) - Enhanced TypeScript

#### GraphQL Extensions:

- **GraphQL** (`graphql.vscode-graphql`) - GraphQL language support
- **GraphQL Syntax** (`graphql.vscode-graphql-syntax`) - Syntax highlighting
- **Apollo GraphQL** (`apollographql.vscode-apollo`) - Apollo-specific features

#### Helpful Extensions:

- **DotENV** (`mikestead.dotenv`) - Environment file syntax
- **GitLens** (`eamodio.gitlens`) - Git supercharged
- **Error Lens** (`usernamehw.errorlens`) - Inline error display
- **Path Intellisense** (`christian-kohler.path-intellisense`) - Autocomplete paths

### 2. Update VS Code Settings

The `.vscode/settings.json` file is already configured with optimal settings for this project. It will:

- ✅ Format on save using Prettier
- ✅ Auto-fix ESLint errors on save
- ✅ Organize imports automatically
- ✅ Use 2 spaces for indentation
- ✅ Show type hints and parameter names
- ✅ Enable ESLint flat config support

### 3. Verify Setup

Open Command Palette (`Cmd/Ctrl + Shift + P`) and run:

```
Developer: Reload Window
```

## 🎯 Key Features Configured

### Auto-Format on Save

Every time you save a file:

- Prettier formats the code
- ESLint fixes auto-fixable issues
- Imports are organized
- Trailing whitespace is removed

### TypeScript IntelliSense

Enhanced TypeScript features:

- Parameter name hints
- Variable type hints
- Function return type hints
- Auto-import suggestions
- Quick fixes and refactoring

### ESLint Integration

- Real-time linting feedback
- Squiggly lines under errors
- Auto-fix on save
- Supports ESLint 9 flat config

### Prisma Support

- Syntax highlighting for `.prisma` files
- Auto-formatting Prisma schemas
- IntelliSense for Prisma models

## 🐛 Debugging

### Launch Configurations Available:

1. **Launch Server** - Start server with hot reload
   - Press `F5` or use Debug panel
   - Automatically restarts on file changes
   - Breakpoints work in TypeScript files

2. **Launch Server (No Watch)** - Start without hot reload
   - Good for testing production-like behavior

3. **Attach to Process** - Attach to running Node process
   - Start server with `--inspect` flag first

4. **Debug Current File** - Debug any TypeScript file
   - Open file and press `F5`

## ⚙️ Tasks (Cmd/Ctrl + Shift + B)

Pre-configured tasks you can run:

| Task             | Description              | Shortcut               |
| ---------------- | ------------------------ | ---------------------- |
| Start Dev Server | Run `npm run dev`        | -                      |
| Build            | Compile TypeScript       | `Cmd/Ctrl + Shift + B` |
| Lint             | Check for linting errors | -                      |
| Lint Fix         | Auto-fix linting errors  | -                      |
| Format           | Format all files         | -                      |
| Type Check       | Run TypeScript compiler  | -                      |
| Prisma Generate  | Generate Prisma Client   | -                      |
| Prisma Migrate   | Run database migrations  | -                      |
| Prisma Studio    | Open Prisma Studio       | -                      |
| Full Setup       | Complete setup sequence  | -                      |

## 🚨 Troubleshooting

### Debugging not working

1. Ensure `tsx` is installed: `npm install`
2. Check `.env` file exists
3. Verify port 9229 is not in use
4. Check launch configuration in `.vscode/launch.json`

### Prisma syntax highlighting missing

1. Install Prisma extension: `prisma.prisma`
2. Check file extension is `.prisma`
3. Reload window

## 🌟 Pro Tips

### 1. Code Snippets

Type these prefixes and press Tab:

- `log` - console.log()
- `imp` - import statement
- `exp` - export statement
- `func` - function declaration
- `af` - arrow function

# ESLint & Code Quality Guide

## 📋 Overview

This project uses **ESLint 9.37.0** with the new flat config format (`eslint.config.mjs`), comprehensive TypeScript support, security rules, and Prettier integration following modern Node.js best practices.

## 🔧 Installed Plugins & Extensions

### Core Linting

- **eslint** - Base ESLint framework
- **@typescript-eslint/eslint-plugin** - TypeScript-specific linting rules
- **@typescript-eslint/parser** - TypeScript parser for ESLint

### Import Management

- **eslint-plugin-import** - Validates proper imports and enforces ordering
- **eslint-plugin-n** (formerly node) - Node.js specific best practices

### Code Quality

- **eslint-plugin-promise** - Enforces best practices for promises
- **eslint-plugin-security** - Identifies potential security issues

### Formatting

- **prettier** - Opinionated code formatter
- **eslint-config-prettier** - Disables ESLint rules that conflict with Prettier

## 🚀 Quick Start

### Run Linting

```bash
# Check for linting errors
npm run lint

# Auto-fix linting errors
npm run lint:fix

# Format code with Prettier
npm run format

# Check formatting without changing files
npm run format:check

# Type check without building
npm run type-check
```

## 📝 NPM Scripts

| Script                 | Description                    |
| ---------------------- | ------------------------------ |
| `npm run lint`         | Check for ESLint errors        |
| `npm run lint:fix`     | Auto-fix ESLint errors         |
| `npm run format`       | Format code with Prettier      |
| `npm run format:check` | Check if code is formatted     |
| `npm run type-check`   | Run TypeScript compiler checks |

## 🚨 Common Issues & Solutions

### Issue: ESLint is slow

**Solution**: Add more patterns to `.eslintignore` or use `.eslintcache`

```bash
npm run lint -- --cache
```

### Issue: Prettier and ESLint conflicts

**Solution**: We use `eslint-config-prettier` to disable conflicting rules. If issues persist, run:

```bash
npm run lint:fix
npm run format
```

### Issue: Import errors in Prisma generated files

**Solution**: Prisma files are already ignored in `eslint.config.mjs` and `.eslintignore`

### Issue: ESLint 9 migration from v8

**Solution**: This project uses ESLint 9's flat config format. If you have an old `.eslintrc.*` file, remove it as `eslint.config.mjs` takes precedence.

## 📊 Pre-commit Hooks (Optional)

Install husky and lint-staged for automatic linting:

```bash
npm install -D husky lint-staged
npx husky init
```

Add to `package.json`:

```json
{
  "lint-staged": {
    "*.ts": ["eslint --fix", "prettier --write"]
  }
}
```

Create `.husky/pre-commit`:

```bash
#!/usr/bin/env sh
. "$(dirname -- "$0")/_/husky.sh"

npx lint-staged
```

## 🎓 Best Practices

1. **Run linting before commits**: `npm run lint`
2. **Fix auto-fixable issues**: `npm run lint:fix`
3. **Keep rules strict**: Only disable rules when absolutely necessary
4. **Document exceptions**: Use inline comments to explain why rules are disabled
5. **Regular updates**: Keep ESLint and plugins updated
