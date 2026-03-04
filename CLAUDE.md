# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test (all)
./gradlew test

# Test (single class)
./gradlew test --tests "com.neocompany.taroro.SomeTest"

# Start MySQL (required before running the app)
docker-compose up -d
```

## Stack

- **Java 17**, Spring Boot 3.5.11, Gradle
- **MySQL 8.4** via Docker (`docker-compose.yml`), HikariCP pool
- **Spring Security** + stateless JWT (jjwt 0.12.6) + OAuth2 (Google, Kakao, Naver)
- **Lombok** throughout — use `@Getter`, `@Builder`, `@RequiredArgsConstructor`, etc.

## Architecture

Package root: `com.neocompany.taroro`

```
domain/           # JPA entities + repositories per domain (e.g., domain/users/)
global/
  config/         # CorsConfig, PasswordConfig
  security/       # SecurityConfig — main filter chain
  jwt/            # JwtProvider, JwtAuthenticationFilter, TokenService, AuthCookieUtil
  oauth2/         # CustomOauth2UserService, handlers, provider-specific UserInfo
  exception/      # GlobalExceptionHandler, BusinessException, ErrorCode
  response/       # ApiResponse<T> wrapper for all responses
```

## Key Architectural Patterns

### Authentication Flow
- **Stateless** — no HTTP sessions. AT and RT are stored as **HttpOnly cookies** (names: `AT`, `RT`).
- `JwtAuthenticationFilter` runs before every request, extracts the AT cookie, validates it, and sets the `SecurityContext`. Invalid/expired tokens result in anonymous access (no redirect).
- Token issuance goes through `TokenService` → `JwtProvider` → `AuthCookieUtil` (writes the cookies).

### OAuth2 Login Rules (`CustomOauth2UserService`)
1. Email exists with `loginType = "normal"` → block (prevent mixing auth methods)
2. Email exists with matching `loginType` → allow login
3. New email → create user with `ROLE_USER`

OAuth2 social ID is BCrypt-hashed and stored as the `passwordHash` in the format `provider:id`.

### Error Handling
Always throw `BusinessException(ErrorCode.XXX)` or `BusinessException(ErrorCode.XXX, "custom message")`. The `GlobalExceptionHandler` catches it and returns:
```json
{ "code": 400, "status": "BAD_REQUEST", "message": "..." }
```

All successful responses are wrapped in `ApiResponse<T>`.

### Endpoint Security Tiers (in `SecurityConfig`)
- **Public:** `/api/auth/signup`, `/api/auth/login`, `/api/products/**`, `/api/categories/**`, `/api/artists/**`, `/api/reviews/list`, `/api/support/posts/**`, OAuth2 paths, static assets
- **Authenticated (`ROLE_USER`):** profile, carts, payments, wishlists, orders, review write/update
- **Admin (`ROLE_ADMIN`):** `/api/admin/**`, `/api/support/posts/answer/**`

### CORS
Two CORS configs exist in `CorsConfig`:
- **Default** — allows `localhost:5174`, `localhost:8080`, `218.38.136.81:8080`, `https://taro.neocompany.co.kr` with credentials
- **PG webhook** (`/api/payments/webhooks/welcome/**`) — allows `*`, no credentials (required by Toss PG)

## Database

- **DDL:** `spring.jpa.hibernate.ddl-auto=update` — schema auto-updates on boot
- **Timezone:** `Asia/Seoul`
- `User.deleted` + `User.deletedAt` — soft delete pattern

## Configuration Notes

- JWT secret and OAuth2 client secrets are in `application.properties` (not production-safe).
- Both AT and RT TTL are set to 365 days in config — adjust for production.
- Redis (`localhost:6379`, password `1234`) and SMTP (Gmail) are configured but their usage may be in feature branches not yet in `main`.
