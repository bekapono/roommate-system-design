# Roommate Web App - User Onboarding MVP

## Overview
This document outlines the MVP scope, user stories, backend flow, and data model for the User Onboarding.

## User Story MVP
- As a user, I want to register with my email and password, so i can create my account and start using Roommate app
    - Returns a 201 Created with success message
- As a user, I want to log out, so that my session ends and my data remains secure
    - Returns a 200 OK
- As a user, I want to create a new household, so my housemates can join and we can collab
    - Returns a 201, with success message + household id and invite code.
- As a user, I want to join existing household using a code, so I can be apart of my home group.
    - Returns a 200 OK (or 404 if invalid code)
- As a user, I want to receive a clear message if I try to register with an email that's already being used so I know I should log in instead
    - Returns 409 Conflict

## Backend Flow : User Registration
1. Client sends a POST /api/users/register with email and password
2. `UserController` accepts the HTTPS request and maps it to `UserRequestDTO`
3. `UserService`
   4. Validates input
   5. checks if email is being used
   6. hashPassword using BCrypt before saving
   7. Creates a new User object entity
8. `UserRepository` saves the new User object to the database
9. Service returns `UserResponseDTO`
10. Controller sends a 201 Created

## Layered System Architecture (Onboarding MVP)
**Layer Breakdown:**

- **Controller**
    - Receives the HTTP request
    - Maps input to `RegisterRequestDTO`
    - Triggers validation
    - Delegates to service

- **Service**
    - Performs business logic: email check, password hash
    - Converts DTO to domain entity
    - Saves to repository
    - Returns `UserResponseDTO`

- **Repository**
    - Handles persistence using Spring Data JPA

- **Response**
    - Returned to client with HTTP 201 Created

## Validation Strategy – MVP vs. Future Plan
- In the MVP, input validation is handled using annotations on the DTO layer (`@Valid`, `@NotBlank`, `@Email`, etc.).
- Spring’s built-in validation and exception handling automatically catch invalid input before reaching the service layer.
- This enables fast iteration with clean error responses and little boilerplate.

### Future Enhancement
- Validation responsibilities may be moved into dedicated **Validator classes** (e.g., `RegisterValidator`) in the Service layer.
- This will enforce Single Responsibility Principle (SRP), improve testability, and decouple validation logic from data transport objects.
- Validators will encapsulate all business rules (e.g., password strength, domain-specific checks) and may eventually handle i18n or advanced error formatting.

## Spring Boot Module Selection
| Module | Purpose | Reason for Inclusion |
|--------|---------|----------------------|
| `spring-boot-starter-web` | Provides support for building web applications and RESTful APIs | Needed to create endpoints for registration and login |
| `spring-boot-starter-data-jpa` | Enables JPA for entity mapping and database interaction | Used for managing `User` persistence via `UserRepository` |
| `spring-boot-starter-validation` | Adds support for validation annotations like `@Valid`, `@NotBlank`, `@Email` | Planned for input validation on DTOs |
| `spring-boot-starter-security` | Adds Spring Security infrastructure including filter chains and authentication | Planned for future JWT-based login and request protection |
| `spring-boot-devtools` | Provides hot reloading and dev-only features | Improves development speed and productivity |
| `com.h2database:h2` | In-memory database for development/testing | Used for fast iteration and schema verification during onboarding MVP |
| `org.postgresql:postgresql` | PostgreSQL JDBC driver | Intended for future production database connection |
| `spring-boot-starter-test` | Includes JUnit, Mockito, and Spring Test tools | Used for writing and running unit/integration tests |


## Entity: User
- userId: UUID autogen, primary key
- firstname: String[50], not nullable
- lastname: String[50], not nullable
- email: String[x], not nullable, unique
- hashedPassword: String[x], not nullable

### Future Enhancements
- User Entity
  - add 'createdAt' / 'updatedAt' attributes for audit tracking
  - 'isEmailVerified' boolean for account verification
  - possible 'username' for public-facing unique identifier
  - Support Optional phone number
  - 'deleted' flag for soft delete behavior


## JWT Authentication – Design Plan (MVP)

### 1. Token Creation

Once a user successfully logs in, the backend will generate a JWT token. This token will include basic claims such as the user ID (`sub`), issued-at time (`iat`), and expiration time (`exp`). The token will be signed using a secret key stored on the server. The login endpoint will return the token in the response body or potentially set it in a cookie (final decision will be coordinated with frontend). The client will use this token to authenticate future requests.

### 2. Token Storage

For backend purposes, we expect the token to be included in the `Authorization` header using the `Bearer` scheme.

### 3. Token Usage

Any route beyond public endpoints like `/register` and `/login` will require a valid JWT. The backend expects this token to be included in the `Authorization` header for protected routes. Endpoints related to households, chores, bills, and user account actions will all be protected in future phases. If a token is missing or invalid, the server will return a `401 Unauthorized` response.

### 4. Token Validation

Once a client sends a request to a protected endpoint, the backend will validate the JWT before allowing access. Token validation will occur in a security filter that runs before controller logic.

The server will verify:
- That the token is properly signed using the correct secret
- That the token is not expired
- That the structure is valid

If valid, the backend will extract claims from the token payload, including:
- `sub` → the user ID
- `email` (optional)
- `role` (for role-based access down the line)

If the token is invalid, malformed, or expired, the backend will reject the request and return a `401 Unauthorized` status code. This validation will happen on every protected request.
