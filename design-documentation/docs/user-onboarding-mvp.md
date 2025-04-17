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

## System Architecture – Registration Flow

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