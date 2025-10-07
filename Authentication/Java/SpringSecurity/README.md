Full Stack Authentication Application

A complete authentication system built with Spring Boot (backend) and Next.js (frontend), featuring user registration, login, and secure authentication using Spring Security and OAuth2.
Project Overview

This project consists of two main components:

    Backend: Spring Boot REST API with Spring Security, OAuth2, and database-backed authentication
    Frontend: Next.js React application with TypeScript providing login and registration interfaces

Features
Backend Features

    ✅ User registration with duplicate username validation
    ✅ Secure password encryption using BCrypt
    ✅ Spring Security with OAuth2 authentication
    ✅ Database persistence with JPA/Hibernate
    ✅ RESTful API endpoints
    ✅ CORS configuration for frontend integration
    ✅ H2 in-memory database (easily switchable to PostgreSQL/MySQL)

Frontend Features

    ✅ Modern, responsive UI with gradient backgrounds
    ✅ Login page with authentication
    ✅ Registration page with form validation
    ✅ Protected dashboard route
    ✅ Error handling and loading states
    ✅ TypeScript for type safety
    ✅ Next.js App Router
    ✅ Turbopack for fast development

Technology Stack
Backend

    Java 25
    Spring Boot 3.5.6
    Spring Security 6.x
    Spring Data JPA
    H2 Database (in-memory)
    Lombok (for boilerplate reduction)
    Maven (build tool)

Frontend

    Next.js 15
    React 18
    TypeScript 5
    CSS Modules (for styling)
    Turbopack (build tool)

Project Structure
Backend Structure

src/main/java/com/example/oauth2/
├── config/
│   ├── CorsConfig.java              # CORS configuration
│   └── SecurityConfig.java          # Spring Security configuration
├── controller/
│   └── UserController.java          # REST API endpoints
├── dto/
│   ├── RegisterRequest.java         # Registration request DTO
│   └── RegisterResponse.java        # Registration response DTO
├── model/
│   └── User.java                    # User entity/POJO
├── repository/
│   └── UserRepository.java          # JPA repository
├── service/
│   ├── CustomUserDetailsService.java # User authentication service
│   └── UserService.java             # User business logic
└── Application.java                 # Main application class

src/main/resources/
└── application.yml                  # Application configuration

Frontend Structure

src/app/
├── login/
│   ├── page.tsx                     # Login page
│   └── login.module.css             # Login styles
├── register/
│   ├── page.tsx                     # Registration page
│   └── register.module.css          # Registration styles
├── dashboard/
│   ├── page.tsx                     # Protected dashboard
│   └── dashboard.module.css         # Dashboard styles
├── layout.tsx                       # Root layout
├── page.tsx                         # Home (redirects to login)
└── globals.css                      # Global styles

Getting Started
Prerequisites

    Java 25 (JDK installed and JAVA_HOME configured)
    Maven 3.x
    Node.js 18+ and npm

Backend Setup

    Navigate to the backend directory:

bash

   cd backend

    Build the project:

bash

   mvn clean install

    Run the application:

bash

   mvn spring-boot:run

The backend will start on http://localhost:8080

    Access H2 Console (optional):
        URL: http://localhost:8080/h2-console
        JDBC URL: jdbc:h2:mem:userdb
        Username: sa
        Password: (leave empty)

Frontend Setup

    Navigate to the frontend directory:

bash

   cd frontend

    Install dependencies:

bash

   npm install

    Run the development server:

bash

   npm run dev

The frontend will start on http://localhost:3000

    Open your browser: Navigate to http://localhost:3000 - you'll be redirected to the login page

API Endpoints
Public Endpoints
Register User
http

POST /api/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securePass123",
  "firstName": "John",
  "lastName": "Doe"
}

Response:
json

{
  "success": true,
  "message": "User registered successfully",
  "userId": 1
}

Protected Endpoints (Require Authentication)
Get User Profile
http

GET /api/profile
Authorization: Basic base64(username:password)

Response:

Welcome, johndoe!

Secure Endpoint
http

GET /api/secure
Authorization: Basic base64(username:password)

Authentication Flow
Registration Flow

    User fills out registration form (username, password, first name, last name)
    Frontend sends POST request to /api/register
    Backend validates username doesn't exist
    Password is encrypted using BCrypt
    User entity is saved to database
    User is redirected to login page

Login Flow

    User enters credentials on login page
    Frontend sends credentials to /api/profile with Basic Authentication
    Spring Security validates credentials against database
    On success, credentials are stored in localStorage
    User is redirected to dashboard
    Dashboard fetches user profile on load

Protected Routes

    Dashboard checks for stored credentials in localStorage
    Makes authenticated request to verify credentials
    Redirects to login if authentication fails

Database Schema
User Table

Column	Type	Constraints
id	BIGINT	PRIMARY KEY, AUTO_INCREMENT
username	VARCHAR(255)	UNIQUE, NOT NULL
password	VARCHAR(255)	NOT NULL (BCrypt encrypted)
firstName	VARCHAR(255)	NOT NULL
lastName	VARCHAR(255)	NOT NULL
role	VARCHAR(50)	NOT NULL (default: USER)
enabled	BOOLEAN	NOT NULL (default: true)

Security Features
Password Encryption

    Passwords are encrypted using BCrypt with strength 10
    BCrypt is a modern, adaptive hashing function designed for password storage
    Automatically handles salt generation and verification

Authentication

    Uses Spring Security with Basic Authentication
    Credentials are Base64 encoded (should use HTTPS in production)
    Stateless session management (no server-side sessions)

CORS Configuration

    Configured to allow requests from http://localhost:3000
    Allows credentials for authenticated requests
    Supports all standard HTTP methods

Configuration
Backend Configuration (application.yml)
yaml

spring:
  datasource:
    url: jdbc:h2:mem:userdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

Switching to PostgreSQL

    Update pom.xml:

xml

   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
   </dependency>

    Update application.yml:

yaml

   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/authdb
       username: your_username
       password: your_password
       driver-class-name: org.postgresql.Driver

Development Tips
Hot Reload

    Backend: Spring Boot DevTools enables automatic restart on code changes
    Frontend: Next.js with Turbopack provides fast refresh on file changes

Testing Registration

Use cURL to test the registration endpoint:
bash

curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "firstName": "Test",
    "lastName": "User"
  }'

Testing Authentication
bash

curl -u testuser:testpass123 http://localhost:8080/api/profile

Common Issues and Solutions
Issue: 403 Forbidden on Registration

Solution: Ensure CORS is properly configured in CorsConfig.java and the backend is restarted.
Issue: Cannot find SecurityFilterChain

Solution: Make sure you're using Spring Security 6.x (comes with Spring Boot 3.x). Run mvn dependency:tree to verify.
Issue: Frontend can't connect to backend

Solution:

    Verify backend is running on port 8080
    Check CORS configuration allows localhost:3000
    Check browser console for CORS errors

Issue: Passwords not matching on registration

Solution: The frontend validates password matching client-side. Ensure both password fields are filled correctly.
Production Considerations
Backend

    Use a production database (PostgreSQL, MySQL)
    Enable HTTPS/TLS
    Use JWT tokens instead of Basic Authentication
    Add rate limiting for registration/login endpoints
    Implement password strength requirements
    Add email verification for new accounts
    Configure proper CORS origins (not localhost)
    Add logging and monitoring
    Implement refresh tokens
    Add account lockout after failed login attempts

Frontend

    Use secure HTTP-only cookies instead of localStorage
    Implement proper token refresh logic
    Add form validation libraries (e.g., React Hook Form, Zod)
    Add loading skeletons for better UX
    Implement proper error boundaries
    Add analytics and monitoring
    Optimize bundle size
    Add comprehensive testing (Jest, Cypress)

License

This project is open source and available under the MIT License.
Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
Support

For issues or questions, please open an issue on the project repository.
