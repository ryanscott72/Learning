# Authentication Application
Spring Boot GraphQL API with Spring Security, OAuth2, and database-backed authentication

## Features
- ✅ User registration with duplicate username validation
- ✅ Secure password encryption using BCrypt
- ✅ Spring Security with OAuth2 authentication
- ✅ Database persistence with JPA/Hibernate/PostgreSQL
- ✅ GraphQL API with mutations and queries
- ✅ CORS configuration for frontend integration
- ✅ GraphiQL interactive IDE for testing

## Technology Stack
- **Java 25**
- **Spring Boot 3.5.6**
- **Spring Security 6.x**
- **Nimbus JOSE + JWT 9** (for JWT token handling)
- **Spring for GraphQL**
- **GraphQL Java**
- **Spring Data JPA**
- **Hibernate** (for Object-Relational Mapping)
- **PostgreSQL** (Relational Database Management System)
- **Lombok** (for boilerplate reduction)
- **Maven** (build tool)

## GraphQL API

The application uses GraphQL instead of REST for more flexible data fetching and a better developer experience.

### GraphQL Endpoint

All GraphQL requests go to: `http://localhost:8080/graphql`

### Testing with GraphiQL

The application includes GraphiQL, an interactive GraphQL IDE:

1. Navigate to `http://localhost:8080/graphiql`
2. Write and execute GraphQL queries/mutations
3. Explore the schema documentation
4. Test authentication flows

**Note**: Cookies are automatically included in GraphiQL requests after login.

## Authentication Flow

### Registration Flow
1. User fills out registration form (username, password, first name, last name)
2. Frontend sends POST request to `/api/register`
3. Backend validates username doesn't exist
4. Password is encrypted using BCrypt
5. User entity is saved to database
6. User is redirected to login page

### Login Flow
1. User enters credentials on login page
2. Frontend sends credentials to `/api/profile` with Basic Authentication
3. Spring Security validates credentials against database
4. On success, credentials are stored in localStorage
5. User is redirected to dashboard
6. Dashboard fetches user profile on load

### Protected Routes
- Dashboard checks for stored credentials in localStorage
- Makes authenticated request to verify credentials
- Redirects to login if authentication fails

## Security Features

### Password Encryption
- Passwords are encrypted using **BCrypt** with strength 10
- BCrypt is a modern, adaptive hashing function designed for password storage
- Automatically handles salt generation and verification

### Authentication
- Uses **JWT (JSON Web Tokens)** for stateless authentication
- Tokens are signed using HMAC SHA-256 with a secret key
- Tokens expire after 24 hours (configurable)
- After login, only the JWT token is transmitted (not the password)
- **HTTP-only cookies** - JWT stored in HttpOnly cookies (not accessible via JavaScript)
- **XSS Protection** - Cookies cannot be stolen by malicious scripts
- **CSRF Protection** - SameSite cookie attribute prevents cross-site attacks

### Password Transmission Security
**CRITICAL: HTTPS Required for Production**

During login, the password is transmitted to the server for authentication. This is done securely through:

- **Development (localhost)**: Password transmitted over HTTP. This is acceptable since traffic stays on your local machine.
- **Production**: **MUST use HTTPS/TLS** to encrypt all traffic between client and server. This is the industry-standard approach.

**Why this approach is secure:**
1. Password is only sent once during login (not stored or repeatedly transmitted)
2. HTTPS encrypts the entire connection end-to-end
3. After successful login, only the JWT token is used for subsequent requests
4. The token is stored in HTTP-only cookies (not accessible to JavaScript)

**Important**: Never deploy this application to production without HTTPS enabled. All major cloud providers (AWS, Azure, GCP, Heroku, Vercel) provide easy HTTPS setup.

### Public Endpoints and Attack Vectors

The `/api/login` and `/api/register` endpoints are **publicly accessible by design** - they must be accessible to unauthenticated users. This creates potential attack vectors:

#### Potential Security Concerns:

1. **Brute Force Attacks**
   - Attackers can attempt unlimited login attempts to guess passwords
   - **Mitigation**: Implement rate limiting (e.g., max 5 attempts per minute per IP)
   - **Mitigation**: Add account lockout after N failed attempts
   - **Mitigation**: Require strong passwords with complexity requirements

2. **Account Enumeration**
   - Attackers can discover valid usernames by attempting to register existing usernames
   - Current implementation returns "Username already exists" error
   - **Mitigation**: Use generic error messages or implement CAPTCHA

3. **Spam/Fake Account Registration**
   - Bots can create unlimited fake accounts
   - **Mitigation**: Add CAPTCHA (e.g., reCAPTCHA, hCaptcha)
   - **Mitigation**: Email verification before account activation
   - **Mitigation**: Rate limit registration attempts per IP

4. **DDoS/Resource Exhaustion**
   - Attackers can overwhelm the server with registration/login requests
   - **Mitigation**: Rate limiting at application or infrastructure level
   - **Mitigation**: Use CDN/WAF (Cloudflare, AWS WAF) for DDoS protection

5. **Timing Attacks**
   - Different response times for valid vs invalid usernames could leak information
   - **Mitigation**: Ensure consistent response times for all authentication attempts

#### Recommended Production Security Measures:

- [ ] Implement rate limiting (Spring Security, Bucket4j, or API Gateway level)
- [ ] Add CAPTCHA to registration and login forms
- [ ] Implement account lockout after failed login attempts
- [ ] Add email/SMS verification for new accounts
- [ ] Enforce password complexity requirements (length, characters, etc.)
- [ ] Monitor for suspicious activity (repeated failures, unusual patterns)
- [ ] Use Web Application Firewall (WAF) for additional protection
- [ ] Implement IP-based blocking for repeated attacks
- [ ] Add audit logging for security events
- [ ] Consider multi-factor authentication (MFA/2FA)

**Note**: The current implementation prioritizes simplicity and learning. Production applications should implement multiple layers of these security measures based on risk assessment and compliance requirements.

### CORS Configuration
- Configured to allow requests from `http://localhost:3000`
- Allows credentials for authenticated requests
- Supports all standard HTTP methods

## Development Tips

### Hot Reload
Spring Boot DevTools enables automatic restart on code changes

### Testing Registration
Use GraphiQL to test the registration:
```graphql
mutation {
  register(input: {
    username: "testuser"
    password: "testpass123"
    firstName: "Test"
    lastName: "User"
  }) {
    success
    message
    userId
  }
}
```

Or use cURL:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: RegisterInput!) { register(input: $input) { success message userId } }",
    "variables": {
      "input": {
        "username": "testuser",
        "password": "testpass123",
        "firstName": "Test",
        "lastName": "User"
      }
    }
  }'
```

### Testing Authentication
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "query": "mutation($input: LoginInput!) { login(input: $input) { success message username } }",
    "variables": {
      "input": {
        "username": "testuser",
        "password": "testpass123"
      }
    }
  }'
```

Then use the stored cookies for authenticated requests:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "query": "{ profile { username firstName lastName role } }"
  }'
```

## Common Issues and Solutions

### Issue: 403 Forbidden on Registration
**Solution**: Ensure CORS is properly configured in `CorsConfig.java` and the backend is restarted.

### Issue: Cannot find SecurityFilterChain
**Solution**: Make sure you're using Spring Security 6.x (comes with Spring Boot 3.x). Run `mvn dependency:tree` to verify.

### Issue: Frontend can't connect to backend
**Solution**: 
- Verify backend is running on port 8080
- Check CORS configuration allows `localhost:3000`
- Check browser console for CORS errors
- Ensure GraphQL endpoint is accessible at `http://localhost:8080/graphql`

### Issue: GraphQL query returns errors
**Solution**:
- Check the query syntax in GraphiQL
- Verify authentication for protected queries (profile)
- Check server logs for detailed error messages
- Ensure the schema matches your queries

### Issue: Passwords sent in plaintext
**This is expected behavior in development.** 

Passwords are transmitted during login, which is secure when using HTTPS. The application follows industry-standard authentication:

1. **Development (localhost)**: HTTP is acceptable since traffic doesn't leave your machine
2. **Production**: You MUST enable HTTPS to encrypt all traffic

**Why this is the standard approach:**
- The backend needs the plaintext password to verify against the BCrypt hash
- HTTPS encrypts the entire connection, including the password
- After login, only JWT tokens are used (no password retransmission)
- This is how major platforms (Google, GitHub, AWS) handle authentication

**Solution**: Enable HTTPS before deploying to production. See the "Production Considerations" section for HTTPS setup instructions.

## Production Considerations

### Security
- [ ] **Enable HTTPS/TLS** - CRITICAL: Required to encrypt password during login
- [ ] Store JWT secret in secure secrets manager (AWS Secrets Manager, Azure Key Vault, etc.)
- [ ] Generate a strong JWT secret (at least 256 bits)
- [ ] Configure production CORS origins (not localhost)
- [ ] Add rate limiting for registration/login endpoints
- [ ] Implement password strength requirements
- [ ] Add email verification for new accounts
- [ ] Add logging and monitoring
- [ ] Implement refresh tokens for better security
- [ ] Add account lockout after failed login attempts
- [ ] Implement token refresh mechanism