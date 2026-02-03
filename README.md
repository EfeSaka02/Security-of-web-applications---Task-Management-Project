# 📋 Task Management System - REST API

**Student:** Efe Saka  
**Student Number:** 55934  
**Course:** Cloud-Oriented Web Applications  
**Lab:** Lab 12 - JWT Authentication & User Data Isolation

---

## 📖 Table of Contents

- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technologies Used](#-technologies-used)
- [Architecture](#-architecture)
- [Security Implementation](#-security-implementation)
- [Setup & Installation](#-setup--installation)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [Known Limitations](#-known-limitations)
- [Future Enhancements](#-future-enhancements)

---

## 🎯 Project Overview

A secure REST API for task management built with **Spring Boot** and **JWT authentication**. The system implements comprehensive security features including stateless authentication, user data isolation, and multi-level access control.

### What This Project Does

- **User Management**: Secure registration and authentication with BCrypt password hashing
- **Task Management**: Create, read, update, and delete personal tasks
- **Access Control**: Each user can only access their own data (horizontal privilege escalation prevention)
- **Security**: JWT token-based authentication, security headers, input validation, and audit logging

### Why I Built This

This project demonstrates enterprise-level security practices in a RESTful API:
- Stateless authentication for scalability
- Defense-in-depth security architecture
- Industry-standard password hashing
- Comprehensive input validation
- Security event logging for monitoring

---

## ✨ Key Features

### 🔐 Authentication & Authorization

- **JWT Token-Based Authentication**: Stateless authentication using JSON Web Tokens
- **BCrypt Password Hashing**: Industry-standard one-way password encryption
- **Access Token Expiration**: 24-hour token lifetime for security
- **User Data Isolation**: Multi-level access control preventing users from accessing each other's data

### 🛡️ Security Features

- **Input Validation**: Server-side validation using DTO annotations
- **HTTP Security Headers**: Protection against XSS, clickjacking, and MIME sniffing
- **SQL Injection Prevention**: Prepared statements via Spring Data JPA
- **Security Logging**: Failed login attempts and unauthorized access tracking
- **Safe Error Messages**: No stack traces or sensitive information exposed to clients

### 📊 Data Management

- **User Entity**: Username, email, hashed password
- **Task Entity**: Title, description, status, priority, timestamps
- **Foreign Key Constraints**: Referential integrity with cascade delete
- **Database Migrations**: Flyway for version-controlled schema changes

---

## 🛠️ Technologies Used

### Backend Framework
- **Spring Boot 4.0.1**: Modern Java web application framework
  - *Why?* Rapid development, production-ready features, extensive ecosystem
- **Spring Security 7.0.2**: Security framework for authentication and authorization
  - *Why?* Industry standard, comprehensive security features, easy JWT integration
- **Spring Data JPA**: Database abstraction layer
  - *Why?* Reduces boilerplate code, type-safe queries, automatic prepared statements

### Database
- **SQLite 3.47**: Lightweight embedded database
  - *Why?* Zero-configuration, perfect for development and demos, portable
- **Flyway 11.14.1**: Database migration tool
  - *Why?* Version control for database schema, reproducible deployments

### Security
- **JJWT 0.12.6**: Java JWT library for token generation and validation
  - *Why?* Secure JWT implementation, easy to use, well-maintained
- **BCrypt**: Password hashing algorithm (via Spring Security)
  - *Why?* Industry standard, one-way hash, computational cost prevents brute force

### Additional Libraries
- **Hibernate 7.2.0**: ORM framework
- **Lombok 1.18.42**: Boilerplate code reduction
- **Jakarta Validation 3.1.1**: Bean validation

### Frontend
- **HTML/CSS/JavaScript**: Simple web interface for demonstration
  - *Why?* Shows complete user flow, demonstrates token storage and usage

---

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (Browser)                      │
│              HTML/CSS/JavaScript Frontend                │
└─────────────────────────────────────────────────────────┘
                           ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────────┐
│                  SPRING SECURITY LAYER                   │
│           JwtAuthenticationFilter (validate JWT)         │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                   CONTROLLER LAYER                       │
│     AuthController | TaskController | UserController    │
│              (HTTP request handling)                     │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                    SERVICE LAYER                         │
│    UserService | TaskService | CustomUserDetailsService │
│         (Business logic & Access Control)                │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER                        │
│           UserRepository | TaskRepository               │
│              (Database access with JPA)                  │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                    DATABASE (SQLite)                     │
│                  users | tasks | flyway                  │
└─────────────────────────────────────────────────────────┘
```

### Request Flow Example

**User Access Task:**

1. **Client** sends request with JWT token in `Authorization: Bearer TOKEN` header
2. **JwtAuthenticationFilter** validates token signature and expiration
3. **SecurityContext** populated with authenticated user information
4. **Controller** receives request, delegates to Service
5. **Service** extracts user ID from SecurityContext, enforces access control
6. **Repository** queries database with user_id filter
7. **Response** returned only if user owns the requested resource

---

## 🔒 Security Implementation

### Defense-in-Depth Architecture

Security is enforced at **four levels** to ensure data isolation:

#### 1. Database Level
```sql
-- Foreign key constraint
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

-- NOT NULL constraint
user_id INTEGER NOT NULL
```
- Enforces referential integrity
- Prevents orphaned data
- Cascade delete for data cleanup

#### 2. Entity Level
```java
@Column(name = "user_id", nullable = false)
private Long userId;
```
- JPA enforces non-null constraint
- Type safety at object level

#### 3. Repository Level
```java
Optional<Task> findByIdAndUserId(Long id, Long userId);
```
- All queries include user_id filter
- Spring Data JPA generates prepared statements
- Prevents SQL injection

#### 4. Service Level
```java
private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();
    String username = userDetails.getUsername();
    return userService.findByUsername(username).getId();
}
```
- Extracts user ID from JWT token
- Every data access checks ownership
- Business logic enforcement

### Password Security

**BCrypt Hashing:**
```java
user.setPassword(passwordEncoder.encode(plainPassword));
```
- **Algorithm**: BCrypt with salt
- **Cost Factor**: 10 rounds (configurable)
- **One-way**: Cannot be reversed
- **Rainbow Table Resistant**: Salt prevents precomputed attacks

### JWT Token Structure

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 (HEADER)
.
eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczNjcyNTcyN... (PAYLOAD)
.
signature-hash (SIGNATURE)
```

**Payload Contents:**
```json
{
  "sub": "username",
  "iat": 1736725727,  // Issued at timestamp
  "exp": 1736812127   // Expiration timestamp (24 hours)
}
```

**Security Features:**
- **Signature**: HMAC SHA256 prevents tampering
- **Expiration**: 24-hour lifetime reduces risk window
- **Stateless**: No server-side session storage needed

### HTTP Security Headers

Configured in `SecurityConfig.java`:

```java
.headers(headers -> headers
    .frameOptions(frame -> frame.deny())                    // Prevents clickjacking
    .contentSecurityPolicy(csp -> csp                       // Mitigates XSS
        .policyDirectives("default-src 'self'; ...")
    )
    .referrerPolicy(referrer -> referrer                    // Privacy protection
        .policy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
    )
)
```

**Protection Against:**
- **XSS**: Content Security Policy restricts script sources
- **Clickjacking**: X-Frame-Options prevents iframe embedding
- **MIME Sniffing**: X-Content-Type-Options forces declared types

### Input Validation

**Server-Side Validation (DTO):**
```java
public class CreateUserRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

**Benefits:**
- Prevents invalid data from reaching database
- Structured error messages (no stack traces)
- Returns HTTP 400 with validation details

### Security Logging

**What is Logged:**
```java
logger.info("Successful login for user: {}", username);
logger.warn("Failed login attempt for user: {}", username);
logger.warn("Unauthorized access attempt to task: {}", taskId);
```

**What is NEVER Logged:**
- ❌ Passwords (plain or hashed)
- ❌ JWT tokens
- ❌ PII (Personally Identifiable Information)

**Why:** Logs may be stored insecurely or accessed by multiple personnel

---

## 🚀 Setup & Installation

### Prerequisites

- **Java 23** or higher
- **Maven 3.9+**
- **Git**

### Installation Steps

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/task-management.git
cd task-management
```

2. **Build the project**
```bash
./mvnw clean install
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

4. **Access the application**
```
http://localhost:8080
```

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:sqlite:database.db

# JWT Configuration
jwt.secret=your-secret-key-here-minimum-256-bits
jwt.expiration=86400000

# Server Port
server.port=8080
```

**Security Note:** Change `jwt.secret` in production!

---

## 📡 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securepass123"
}
```

**Success Response (201 Created):**
```json
{
  "token": null,
  "username": "john_doe",
  "message": "User registered successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "username": "Username already exists: john_doe"
}
```

---

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securepass123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "message": "Login successful"
}
```

**Error Response (401 Unauthorized):**
```json
"Invalid username or password"
```

---

### Task Endpoints

**All task endpoints require JWT token in Authorization header:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Get All Tasks
```http
GET /api/tasks
Authorization: Bearer TOKEN
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive README",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "userId": 1,
    "createdAt": "2026-01-20T10:30:00",
    "updatedAt": "2026-01-20T10:30:00"
  }
]
```

---

#### Get Task by ID
```http
GET /api/tasks/{id}
Authorization: Bearer TOKEN
```

**Success Response (200 OK):** Returns single task object

**Error Response (404 Not Found):**
```json
"Task not found or access denied"
```

---

#### Create Task
```http
POST /api/tasks
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "title": "New task",
  "description": "Task description",
  "status": "TODO",
  "priority": "MEDIUM"
}
```

**Valid Status Values:** `TODO`, `IN_PROGRESS`, `DONE`  
**Valid Priority Values:** `LOW`, `MEDIUM`, `HIGH`

**Success Response (201 Created):** Returns created task object

**Error Response (400 Bad Request):**
```json
{
  "title": "Title must be between 3 and 200 characters"
}
```

---

#### Update Task
```http
PUT /api/tasks/{id}
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "status": "DONE"
}
```

**Note:** Partial updates supported - only send fields to update

**Success Response (200 OK):** Returns updated task object

---

#### Delete Task
```http
DELETE /api/tasks/{id}
Authorization: Bearer TOKEN
```

**Success Response (200 OK):**
```json
"Task deleted successfully"
```

---

#### Filter Tasks by Status
```http
GET /api/tasks/status/{status}
Authorization: Bearer TOKEN
```

**Example:** `GET /api/tasks/status/TODO`

---

#### Filter Tasks by Priority
```http
GET /api/tasks/priority/{priority}
Authorization: Bearer TOKEN
```

**Example:** `GET /api/tasks/priority/HIGH`

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_tasks_user_id ON tasks(user_id);
```

### Relationships

```
users (1) ──────< (N) tasks
  │
  └─ One user can have many tasks
     Tasks belong to exactly one user
     Cascade delete: deleting user deletes their tasks
```

---

## 📁 Project Structure

```
task-management/
├── src/
│   ├── main/
│   │   ├── java/com/example/task_management/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java          # Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   │   ├── TaskController.java          # Task CRUD endpoints
│   │   │   │   ├── UserController.java          # User endpoints
│   │   │   │   └── HelloController.java         # Test endpoint
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java            # Login response
│   │   │   │   ├── CreateTaskRequest.java       # Task creation DTO
│   │   │   │   ├── CreateUserRequest.java       # User registration DTO
│   │   │   │   ├── LoginRequest.java            # Login DTO
│   │   │   │   └── UpdateTaskRequest.java       # Task update DTO
│   │   │   ├── model/
│   │   │   │   ├── Task.java                    # Task entity
│   │   │   │   └── User.java                    # User entity
│   │   │   ├── repository/
│   │   │   │   ├── TaskRepository.java          # Task data access
│   │   │   │   └── UserRepository.java          # User data access
│   │   │   ├── security/
│   │   │   │   └── JwtAuthenticationFilter.java # JWT validation filter
│   │   │   ├── service/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── TaskService.java             # Task business logic
│   │   │   │   └── UserService.java             # User business logic
│   │   │   ├── util/
│   │   │   │   └── JwtUtil.java                 # JWT token utilities
│   │   │   └── TaskManagementApplication.java   # Main application
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       ├── db/migration/
│   │       │   ├── V1__Create_users_table.sql   # User table migration
│   │       │   └── V2__Create_tasks_table.sql   # Task table migration
│   │       └── static/
│   │           └── index.html                    # Frontend interface
│   └── test/
│       └── java/...                              # Unit tests (future)
├── database.db                                    # SQLite database
├── pom.xml                                        # Maven dependencies
└── README.md                                      # This file
```

---

## ⚠️ Known Limitations

### 1. No Refresh Token Implementation
**Current State:** Access tokens expire after 24 hours, requiring re-login

**Impact:**
- Users must re-enter credentials after token expiration
- Less convenient user experience

**Justification:**
- Focused on core security features (authentication, authorization, data isolation)
- Refresh tokens add complexity (database storage, rotation logic, revocation)
- Access token expiration demonstrates token lifecycle management

**Future Enhancement:** Implement refresh token with rotation for production use

---

### 2. Single User Role
**Current State:** All users have the same "USER" role

**Impact:**
- No administrator or moderator capabilities
- Cannot implement role-based access control (RBAC)

**Justification:**
- Project focuses on **user data isolation** (horizontal access control)
- Demonstrates that users cannot access each other's data
- Role-based authorization (vertical access control) is a separate concern

**Future Enhancement:** Add ADMIN role with user management capabilities

---

### 3. Simple Frontend
**Current State:** Basic HTML/CSS/JavaScript interface

**Impact:**
- Limited UI/UX features
- No modern framework benefits (React, Vue, Angular)

**Justification:**
- Backend security is the primary focus
- Simple frontend demonstrates API usage clearly
- Reduces complexity for security demonstrations

**Future Enhancement:** Rebuild with React for production-ready interface

---

### 4. SQLite Database
**Current State:** Using embedded SQLite database

**Impact:**
- Single-file database, not suitable for production
- Limited concurrency support
- No network access

**Justification:**
- Zero configuration, perfect for development and demos
- Portable (entire database in one file)
- Sufficient for demonstrating security features

**Production Recommendation:** Use PostgreSQL or MySQL

---

## 🔮 Future Enhancements

### Security Enhancements
- [ ] **Refresh Token Implementation**
  - Short-lived access tokens (15 minutes)
  - Long-lived refresh tokens (7 days)
  - Token rotation on refresh
  - Database-backed revocation

- [ ] **Account Security**
  - Account lockout after failed login attempts
  - Email verification on registration
  - Password reset functionality
  - Two-factor authentication (2FA)

- [ ] **Advanced Authorization**
  - Role-Based Access Control (ADMIN, USER, MODERATOR)
  - Permission-based actions
  - Team collaboration with shared tasks

### Features
- [ ] **Task Enhancements**
  - Due dates and reminders
  - Task categories/tags
  - File attachments
  - Subtasks and dependencies

- [ ] **Collaboration**
  - Share tasks with other users
  - Comments and activity log
  - Team workspaces

- [ ] **Search & Filter**
  - Full-text search
  - Advanced filtering (date range, multiple statuses)
  - Sorting options

### Technical Improvements
- [ ] **Testing**
  - Unit tests (JUnit 5)
  - Integration tests
  - Security tests (OWASP)

- [ ] **API Documentation**
  - Swagger/OpenAPI integration
  - Interactive API explorer

- [ ] **Monitoring**
  - Metrics (Spring Actuator)
  - Centralized logging (ELK stack)
  - Performance monitoring

- [ ] **Production Readiness**
  - PostgreSQL database
  - Redis for session management
  - Docker containerization
  - Kubernetes deployment
  - CI/CD pipeline (GitHub Actions)

---

## 👨‍💻 Developer

**Efe Saka**  
Student Number: 55934  
Cloud-Oriented Web Applications Course

---

## 📝 License

This project is developed as part of academic coursework.

---

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **JJWT Library** for robust JWT implementation
- **Course Instructor** for guidance and requirements
- **Stack Overflow Community** for troubleshooting help

---

## 📞 Contact

For questions or feedback regarding this project:
- **Student:** Efe Saka
- **Student Number:** 55934
