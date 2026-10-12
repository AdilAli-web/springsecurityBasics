# 🔐 Spring Security Basics — JWT Authentication & Authorization

A practical Spring Boot project for learning and implementing **Spring Security with JWT-based authentication and role-based authorization**.

This project demonstrates how a request moves through Spring Security, how users are loaded from MySQL, how passwords are protected using BCrypt, how JWTs are generated and validated, and how roles such as `USER` and `ADMIN` are used to protect APIs.

> **Important:** The active JWT implementation in this project uses **Spring Security's `JwtEncoder` and `JwtDecoder` with Nimbus**, not the older custom JJWT `TokenService` approach. `TokenService.java` is kept in the repository as commented-out/previous code for comparison.

---

## 📌 What this project teaches

- Spring Security fundamentals
- Authentication vs Authorization
- `SecurityFilterChain`
- `UserDetailsService`
- `UserDetails`
- `DaoAuthenticationProvider`
- `AuthenticationProvider`
- `AuthenticationManager`
- `PasswordEncoder`
- BCrypt password hashing
- JWT generation
- JWT claims
- JWT encoding and decoding
- JWT signature validation
- Spring OAuth2 Resource Server
- `JwtEncoder`
- `JwtDecoder`
- `JwtAuthenticationConverter`
- `JwtGrantedAuthoritiesConverter`
- Role Based Access Control (RBAC)
- `SecurityContext`
- Stateless authentication
- CSRF configuration
- CORS configuration
- JPA + MySQL user persistence
- DTOs
- Controller → Service → Repository architecture
- Principal access in controllers

---

# 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Security | Authentication and authorization |
| Spring Security OAuth2 Resource Server | JWT resource-server support |
| Spring Security OAuth2 JOSE | JWT encoding/decoding support |
| Spring Data JPA | Database access |
| MySQL | User/admin persistence |
| BCrypt | Password hashing |
| JJWT 0.12.6 | Included dependency / previous JWT approach |
| Lombok | Boilerplate reduction |
| Maven | Dependency and build management |

---

# 🏗️ Project Structure

```text
demo/
└── src/
    └── main/
        ├── java/com/example/demo/
        │   ├── Config/
        │   │   ├── SpringConfig.java
        │   │   └── CustomerDetails.java
        │   │
        │   ├── Controller/
        │   │   ├── AuthController.java
        │   │   └── TestController.java
        │   │
        │   ├── Dto/
        │   │   ├── RegisterRequest.java
        │   │   ├── loginRequest.java
        │   │   └── loginResponse.java
        │   │
        │   ├── Entity/
        │   │   ├── User.java
        │   │   ├── Admin.java
        │   │   └── Role.java
        │   │
        │   ├── Repo/
        │   │   ├── UserRepo.java
        │   │   └── AdminRepo.java
        │   │
        │   └── Service/
        │       ├── AuthService.java
        │       ├── UserService.java
        │       ├── AdminService.java
        │       ├── TokenGenerator.java
        │       └── TokenService.java
        │
        └── resources/
            └── application.yaml
```

---

# 🔐 Core Security Architecture

The project has two major security phases:

## 1. Authentication

Authentication answers:

> **Who are you?**

The client sends an email and password.

```text
Client
  │
  │ email + password
  ▼
AuthController
  │
  ▼
AuthService
  │
  ▼
AuthenticationManager
  │
  ▼
DaoAuthenticationProvider
  │
  ▼
CustomerDetails
  │
  ▼
UserRepo
  │
  ▼
MySQL
```

Spring loads the user's stored password and role. The configured BCrypt encoder checks the submitted password against the stored BCrypt hash.

If authentication succeeds:

```text
Authentication
      │
      ▼
TokenGenerator
      │
      ▼
JwtEncoder
      │
      ▼
Signed JWT
      │
      ▼
Client
```

---

# 2. Authorization

Authorization answers:

> **What is this authenticated user allowed to access?**

For later requests:

```text
Client
  │
  │ Authorization: Bearer <JWT>
  ▼
Spring Security Filter Chain
  │
  ▼
OAuth2 Resource Server
  │
  ▼
JwtDecoder
  │
  ▼
JWT verified
  │
  ▼
JwtAuthenticationConverter
  │
  ▼
Authorities / Role
  │
  ▼
Authorization rules
  │
  ├── USER
  └── ADMIN
```

Only after the security checks succeed does the request reach the controller.

---

# 🔄 Complete Registration Flow

The project exposes:

```http
POST /api/auth/register
```

### Request

```json
{
  "name": "Adil",
  "email": "adil@example.com",
  "password": "mypassword",
  "role": "USER"
}
```

### Flow

```text
POST /api/auth/register
        │
        ▼
AuthController.register()
        │
        ▼
UserService.registers()
        │
        ├── lowercase email
        │
        ├── check existsByEmail()
        │
        ├── BCrypt encode password
        │
        ├── set name/email/password/role
        │
        ▼
UserRepo.save()
        │
        ▼
MySQL
```

### Why BCrypt?

The database should **not store the original password**.

Instead:

```text
Plain password
      │
      ▼
BCrypt
      │
      ▼
Password hash
      │
      ▼
Database
```

During login, Spring does not decrypt BCrypt. BCrypt is a password hashing algorithm. The submitted password is verified against the stored hash.

---

# 🔑 Login Flow

The login endpoint is:

```http
POST /api/auth/login
```

### Request

```json
{
  "email": "adil@example.com",
  "password": "mypassword"
}
```

### Step-by-step

### 1. Controller

`AuthController.login()` receives the request and delegates to `AuthService`.

### 2. Authentication token

The service creates:

```java
new UsernamePasswordAuthenticationToken(
    loginRequest.getEmail(),
    loginRequest.getPassword()
)
```

This object represents the login credentials.

### 3. AuthenticationManager

`AuthenticationManager.authenticate(...)` starts the authentication process.

### 4. AuthenticationProvider

The project explicitly configures a `DaoAuthenticationProvider`.

```java
DaoAuthenticationProvider provider =
    new DaoAuthenticationProvider(userDetailsService);

provider.setPasswordEncoder(passwordEncoder);
```

The provider uses:

- `UserDetailsService` to find the user
- `PasswordEncoder` to verify the password

### 5. UserDetailsService

The custom implementation is:

```text
CustomerDetails
```

It calls:

```java
userRepo.findByEmail(email.toLowerCase())
```

and converts the database user into Spring Security's `UserDetails`.

### 6. UserDetails

The returned security user contains:

- username
- password
- roles
- enabled/disabled state

The project's role mapping is:

```java
.roles(user.getRole().name())
```

So the database role is turned into a Spring role such as `ROLE_USER` or `ROLE_ADMIN`.

### 7. Password verification

The configured `BCryptPasswordEncoder` checks the submitted password against the database hash.

### 8. Authentication success

When authentication succeeds, Spring creates an authenticated `Authentication` object.

### 9. JWT generation

`AuthService` passes that authentication object to:

```text
TokenGenerator
```

---

# 🪙 JWT Generation

The active implementation is in:

```text
Service/TokenGenerator.java
```

It receives:

```java
Authentication authentication
```

Then extracts the user's authority:

```java
String role = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst()
        .orElse("");
```

Then it creates JWT claims:

```java
JwtClaimsSet.builder()
    .subject(authentication.getName())
    .claim("role", role)
    .issuedAt(now)
    .expiresAt(now.plusSeconds(600))
    .build();
```

Therefore the token contains information similar to:

```json
{
  "sub": "adil@example.com",
  "role": "ROLE_ADMIN",
  "iat": "...",
  "exp": "..."
}
```

The actual encoded token is generated through:

```text
JwtEncoder
   ↓
NimbusJwtEncoder
   ↓
HS256
   ↓
Signed JWT
```

The token is returned by:

```json
{
  "jwt": "<token>"
}
```

---

# 🔏 JWT Signing

The project creates a secret key using:

```java
Keys.hmacShaKeyFor(
    secret.getBytes(StandardCharsets.UTF_8)
)
```

That secret key is shared by:

```text
JwtEncoder  → signs JWT
JwtDecoder  → verifies JWT
```

The project uses:

```text
HS256
```

HS256 is a symmetric HMAC algorithm.

That means the same secret material is used to sign and verify the token.

---

# 🔍 JWT Validation

When a client calls a protected API:

```http
Authorization: Bearer <JWT>
```

Spring Security's OAuth2 Resource Server handles JWT authentication.

The configured resource-server section is:

```java
.oauth2ResourceServer(oauth2 ->
    oauth2.jwt(jwt ->
        jwt.jwtAuthenticationConverter(
            jwtAuthenticationConverter()
        )
    )
)
```

### What happens internally?

```text
HTTP Request
    │
    ▼
Bearer token found
    │
    ▼
JWT authentication processing
    │
    ▼
JwtDecoder
    │
    ├── verify signature
    ├── validate token structure
    └── validate expiration
    │
    ▼
JwtAuthenticationConverter
    │
    ▼
Authentication
    │
    ▼
SecurityContext
    │
    ▼
Authorization
    │
    ▼
Controller
```

---

# 🧩 JwtDecoder

The project defines:

```java
@Bean
JwtDecoder jwtDecoder(SecretKey secretKey) {
    return NimbusJwtDecoder
            .withSecretKey(secretKey)
            .build();
}
```

### Why is it needed?

The JWT is not trusted just because the client sends it.

Spring must verify that:

1. the JWT was signed using the expected secret,
2. the signature is valid,
3. the token is structurally valid,
4. the token has not expired.

That is why the server needs a decoder/validator.

---

# 🧩 JwtAuthenticationConverter

The project's token contains a custom claim named:

```text
role
```

So the project configures:

```java
authoritiesConverter.setAuthoritiesClaimName("role");
authoritiesConverter.setAuthorityPrefix("");
```

This tells Spring to read authorities from your custom `role` claim without adding another prefix during conversion.

Then:

```java
converter.setJwtGrantedAuthoritiesConverter(
    authoritiesConverter
);
```

This converts JWT claims into Spring Security authorities.

---

# 👤 Role-Based Authorization

The project has:

```java
public enum Role {
    USER,
    ADMIN
}
```

The security rules include:

```java
.requestMatchers("/api/v1/user/admin")
.hasRole("ADMIN")
```

That means the protected admin endpoint requires the ADMIN role.

---

# 🛡️ SecurityFilterChain

`SecurityFilterChain` is the central place where this project defines HTTP security rules.

Important configuration:

```java
.csrf(csrf -> csrf.disable())
.cors(cors -> cors.disable())
.sessionManagement(session ->
    session.sessionCreationPolicy(
        SessionCreationPolicy.STATELESS
    )
)
```

and:

```java
.authorizeHttpRequests(...)
```

and:

```java
.oauth2ResourceServer(...)
```

Think of it as the application's security gate.

---

# 🚦 Endpoint Authorization Rules

| Endpoint | Rule |
|---|---|
| `/api/v1/user/get` | Public |
| `/api/auth/register` | Public |
| `/api/auth/login` | Public |
| `/api/auth/home` | Public |
| `/api/v1/user/admin` | ADMIN only |
| `/api/v1/user/admin/create` | Public |
| `/api/v1/user/login` | Public |
| Everything else | Authenticated |

### Important security note

`/api/v1/user/admin/create` is currently configured with `permitAll()`.

Despite its name, **it is not currently restricted to ADMIN users**.

If the intention is to make it admin-only, change the rule to:

```java
.requestMatchers("/api/v1/user/admin/create")
.hasRole("ADMIN")
```

This README documents the configuration that is actually present in the repository.

---

# 🧠 AuthenticationManager

The project explicitly creates:

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationProvider authenticationProvider) {

    return new ProviderManager(authenticationProvider);
}
```

### Why?

The `AuthenticationManager` is responsible for coordinating authentication.

Conceptually:

```text
AuthenticationManager
       │
       ▼
AuthenticationProvider
       │
       ▼
UserDetailsService + PasswordEncoder
```

Your `AuthService` calls it directly.

---

# 🧩 DaoAuthenticationProvider

This component connects normal username/password authentication with your database-backed user details.

It uses:

```text
UserDetailsService
+
PasswordEncoder
```

Therefore:

```text
Email/password
     ↓
AuthenticationManager
     ↓
DaoAuthenticationProvider
     ↓
CustomerDetails
     ↓
UserRepo
     ↓
MySQL
```

---

# 👨‍💻 CustomerDetails

The project implements:

```java
UserDetailsService
```

in:

```text
CustomerDetails.java
```

The important method is:

```java
loadUserByUsername(String email)
```

Although the method is named `loadUserByUsername`, this application uses **email as the login identifier**.

The database user becomes Spring Security's security user:

```text
Database User
     ↓
CustomerDetails
     ↓
Spring Security UserDetails
```

---

# ✅ Enabled / Disabled User

The database entity contains:

```java
private boolean enabled = true;
```

and the security user is created with:

```java
.disabled(!user.isEnabled())
```

So:

```text
enabled = true
    ↓
Security account enabled

enabled = false
    ↓
Security account disabled
```

---

# 🔒 Stateless Authentication

The project uses:

```java
SessionCreationPolicy.STATELESS
```

### What does this mean?

The server does not maintain the normal HTTP login session for authentication.

Instead, each protected request carries its own JWT:

```text
Request 1 → JWT
Request 2 → JWT
Request 3 → JWT
```

The server validates the token for each protected request.

---

# 🛑 CSRF

The project disables CSRF:

```java
.csrf(csrf -> csrf.disable())
```

This project uses stateless Bearer-token authentication rather than relying on a browser session cookie for API authentication.

For a cookie-based browser application, the CSRF decision can be different.

---

# 🌐 CORS

The current project also explicitly disables CORS through Spring Security:

```java
.cors(cors -> cors.disable())
```

CORS and authentication are different concepts:

```text
CORS
→ Can this browser origin call my server?

Authentication
→ Who is this user?

Authorization
→ What is this user allowed to do?
```

---

# 🧱 Controller → Service → Repository Architecture

The project follows a common layered backend structure:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Example:

```text
AuthController
     ↓
AuthService
     ↓
AuthenticationManager
     ↓
Security components
```

For registration:

```text
AuthController
     ↓
UserService
     ↓
UserRepo
     ↓
MySQL
```

For admin data:

```text
TestController
     ↓
AdminService
     ↓
AdminRepo
     ↓
MySQL
```

---

# 🗃️ Database Model

## User

The `User` entity stores:

- id
- name
- email
- password
- role
- enabled

The password is stored in encoded form.

The role is stored as an enum string:

```text
USER
ADMIN
```

## Admin

The `Admin` entity stores:

- id
- PatientName
- RoomName

---

# 📦 DTOs

The project uses DTOs for authentication requests/responses.

### RegisterRequest

```java
record RegisterRequest(
    String name,
    String email,
    String password,
    Role role
)
```

### loginRequest

Contains:

```text
email
password
```

### loginResponse

Contains:

```text
jwt
```

This keeps API input/output separate from persistence entities.

---

# 🔌 API Endpoints

## Register

```http
POST /api/auth/register
```

Example:

```json
{
  "name": "Adil",
  "email": "adil@example.com",
  "password": "password",
  "role": "USER"
}
```

---

## Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "adil@example.com",
  "password": "password"
}
```

Response:

```json
{
  "jwt": "<JWT>"
}
```

---

## Public API

```http
GET /api/v1/user/get
```

---

## Admin API

```http
GET /api/v1/user/admin
```

Requires ADMIN authority.

---

## Create Admin Data

```http
POST /api/v1/user/admin/create
```

**Current project configuration:** public endpoint.

---

## Home

```http
GET /api/auth/home
```

Public.

---

# 🧪 Postman Flow

### Step 1 — Register

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

### Step 2 — Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

Copy the returned JWT.

### Step 3 — Call protected endpoint

```http
GET http://localhost:8080/api/v1/user/admin
Authorization: Bearer <JWT>
```

### Result

If the JWT is valid and contains the required role:

```text
200 OK
```

If the user is authenticated but does not have ADMIN authority:

```text
403 Forbidden
```

If authentication is missing/invalid:

```text
401 Unauthorized
```

---

# 🔁 Complete Request Lifecycle

## Login

```text
                  LOGIN
                    │
                    ▼
        POST /api/auth/login
                    │
                    ▼
             AuthController
                    │
                    ▼
              AuthService
                    │
                    ▼
         UsernamePasswordToken
                    │
                    ▼
        AuthenticationManager
                    │
                    ▼
      DaoAuthenticationProvider
                    │
          ┌─────────┴─────────┐
          │                   │
          ▼                   ▼
  UserDetailsService     PasswordEncoder
          │                   │
          ▼                   │
       UserRepo               │
          │                   │
          ▼                   │
        MySQL                 │
          │                   │
          └─────────┬─────────┘
                    ▼
          Authentication SUCCESS
                    │
                    ▼
             TokenGenerator
                    │
                    ▼
               JwtEncoder
                    │
                    ▼
                 JWT
                    │
                    ▼
                Client
```

## Protected request

```text
Client
  │
  │ Bearer JWT
  ▼
Spring Security Filter Chain
  │
  ▼
OAuth2 Resource Server
  │
  ▼
JwtDecoder
  │
  ├── Signature verification
  ├── Expiration validation
  └── JWT validation
  │
  ▼
JwtAuthenticationConverter
  │
  ▼
role claim
  │
  ▼
GrantedAuthority
  │
  ▼
Authorization rules
  │
  ├── permitAll()
  ├── authenticated()
  └── hasRole("ADMIN")
  │
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Repository
  │
  ▼
Database
```

---

# 🧠 Why Do We Need Each Component?

| Component | Why it exists |
|---|---|
| `SecurityFilterChain` | Defines HTTP security rules |
| `AuthenticationManager` | Starts and coordinates authentication |
| `DaoAuthenticationProvider` | Performs username/password authentication using user details + password encoder |
| `UserDetailsService` | Loads the user for Spring Security |
| `CustomerDetails` | Connects your `User` entity to Spring Security |
| `PasswordEncoder` | Safely verifies password hashes |
| `BCryptPasswordEncoder` | Provides BCrypt password hashing |
| `JwtEncoder` | Creates signed JWTs |
| `JwtDecoder` | Verifies and parses JWTs |
| `JwtAuthenticationConverter` | Converts JWT claims into Spring Security authentication |
| `JwtGrantedAuthoritiesConverter` | Converts the role claim into authorities |
| `SecurityContext` | Holds the authenticated principal during the request |
| `SessionCreationPolicy.STATELESS` | Makes the API token-based instead of session-based |
| `UserRepo` | Loads and stores users |
| `UserService` | Handles registration business logic |
| `AuthService` | Handles login/authentication |
| `TokenGenerator` | Builds the JWT claims and encodes the token |
| `Role` | Defines application roles |
| DTOs | Separate API contracts from database entities |

---

# ⚖️ Authentication vs Authorization

## Authentication

```text
Email + Password
      ↓
Can Spring prove who you are?
      ↓
YES
      ↓
JWT issued
```

## Authorization

```text
JWT
 ↓
Who are you?
 ↓
What role do you have?
 ↓
Are you allowed to call this endpoint?
```

Example:

```text
USER
 ↓
GET /api/v1/user/admin
 ↓
authenticated? YES
 ↓
ADMIN authority? NO
 ↓
403 Forbidden
```

---

# 🧩 Spring Security Mental Model

Remember these four layers:

```text
1. Authentication
   "Who are you?"

2. Token
   "How will the next request prove it?"

3. Authorization
   "What are you allowed to access?"

4. Application
   "What business logic should execute?"
```

---

# 🔐 JWT Mental Model

A JWT is conceptually:

```text
HEADER.PAYLOAD.SIGNATURE
```

### Header

Describes token metadata such as the signing algorithm.

### Payload

Contains claims such as:

```text
sub
role
iat
exp
```

### Signature

Proves that the token was signed using the expected secret and that its signed content has not been altered.

JWT payload data is **encoded, not secret/encrypted by default**. Do not put passwords or sensitive secrets into JWT claims.

---

# 🆚 Current JWT Implementation vs Old TokenService

The repository contains two approaches.

## ✅ Active approach

```text
TokenGenerator
     ↓
JwtEncoder
     ↓
NimbusJwtEncoder
     ↓
JWT
```

## 🗂️ Previous approach

```text
TokenService
     ↓
JJWT / io.jsonwebtoken
     ↓
Jwts.builder()
     ↓
JWT
```

The old `TokenService.java` is commented out.

The active implementation is the Spring Security `JwtEncoder` route.

---

# 🛡️ Security Considerations

### Do not commit production secrets

The repository's `application.yaml` currently contains database credentials and a JWT secret.

For a real project:

- move secrets to environment variables
- use a secret manager
- never commit real production passwords
- rotate exposed credentials

Example:

```yaml
jwt:
  secret: ${JWT_SECRET}
```

and:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

---

# 🚀 Run the Project

### 1. Start MySQL

Create/start MySQL and make sure the application can connect to:

```text
springSecurity
```

### 2. Check configuration

Update your local:

```text
src/main/resources/application.yaml
```

with your MySQL username/password and a secure JWT secret.

### 3. Start Spring Boot

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Or run `DemoApplication.java` from the IDE.

---

# 🎯 Interview Explanation

A strong way to explain this project in an interview:

> "I implemented JWT-based authentication using Spring Security. During registration, the user's password is encoded with BCrypt before it is stored in MySQL. During login, the AuthenticationManager delegates to a DaoAuthenticationProvider, which uses a custom UserDetailsService to load the user and a PasswordEncoder to verify the password. After successful authentication, I generate a JWT using Spring Security's JwtEncoder. For subsequent protected requests, the client sends the JWT as a Bearer token. Spring Security's OAuth2 Resource Server uses JwtDecoder to validate the token and a JwtAuthenticationConverter to convert the role claim into Spring Security authorities. Authorization rules in SecurityFilterChain then decide whether the request is allowed, for example restricting the admin endpoint to users with the ADMIN role. The API is configured as stateless, so authentication is carried by the JWT rather than an HTTP session."

---

# 📚 Key Concepts to Revise

Before presenting this project, make sure you understand:

1. Authentication vs Authorization
2. AuthenticationManager
3. AuthenticationProvider
4. DaoAuthenticationProvider
5. UserDetailsService
6. UserDetails
7. PasswordEncoder
8. BCrypt
9. JWT
10. JWT signature
11. JWT claims
12. JwtEncoder
13. JwtDecoder
14. OAuth2 Resource Server
15. SecurityFilterChain
16. SecurityContext
17. GrantedAuthority
18. Roles
19. `hasRole()`
20. `permitAll()`
21. `authenticated()`
22. Stateless sessions
23. CSRF
24. CORS
25. 401 vs 403

---

# 🔥 One-Line Architecture

```text
Register → BCrypt → MySQL
Login → AuthenticationManager → UserDetailsService → BCrypt verification → JwtEncoder → JWT
Protected Request → Bearer JWT → JwtDecoder → Converter → Authorities → Authorization → Controller
```

---

## 📌 Repository

[GitHub Repository](https://github.com/AdilAli-web/springsecurityBasics/tree/master/demo)

---

## 👨‍💻 Author

**Adil Ali**

Java / Spring Boot Backend Developer
