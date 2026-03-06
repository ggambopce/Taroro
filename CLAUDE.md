# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**타로 온라인 (Tarot Online)** — 타로/사주 마스터와 사용자를 연결하는 실시간 상담 플랫폼

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
- **MySQL 8.4** (DB: `taro`, Port: 3306) via Docker, HikariCP pool
- **Spring Security** + Custom DB Session (SID 쿠키) + OAuth2 (Google, Kakao, Naver)
- **STOMP WebSocket** — 실시간 상담 채널 (인메모리 브로커 기반 최적화)
- **Springdoc OpenAPI 2.8.5** — Swagger UI (`/swagger`)
- **Lombok** throughout — `@Getter`, `@Builder`, `@RequiredArgsConstructor` 등
- **배포:** PM2, Port 4005

## Naming Conventions

| 대상 | 규칙 | 예시 |
|---|---|---|
| API Endpoint | kebab-case | `/api/auth/user-email/check` |
| Payload / Return | camelCase | `userAge: 28` |
| DB 테이블명 | kebab-case | `user-buy-list` |
| DB 컬럼명 | snake_case | `user_age`, `created_at` |
| 파일/폴더명 | camelCase | `userController.java` |

## Architecture

Package root: `com.neocompany.taroro`

```
domain/           # JPA 엔티티 + 레포지토리 (도메인별)
  users/
    docs/         # Swagger 인터페이스 (컨트롤러 어노테이션 분리)
global/
  config/
    swagger/      # SwaggerConfig — OpenAPI 전역 설정
  security/       # SecurityConfig — 필터 체인
  sessions/       # Session 엔티티, SessionRepository, SessionService
                  # SessionAuthenticationFilter, SessionCookieUtil
  oauth2/         # CustomOauth2UserService, handlers, provider별 UserInfo
  exception/      # GlobalExceptionHandler, BusinessException, ErrorCode
  response/       # ApiResponse<T> — 모든 응답 공통 래퍼
```

## Key Architectural Patterns

### Authentication Flow (Session Cookie 방식)
- `sessions` 테이블을 DB에서 직접 관리. Spring HttpSession 미사용.
- 로그인/OAuth2 성공 시 `SessionService.createSession(userId, ttl)` → UUID `sid` 생성 → `SID` HttpOnly 쿠키 발급 (1년 만료).
- `SessionAuthenticationFilter`가 모든 요청에서 `SID` 쿠키를 읽어 DB 조회 → 만료 시 세션 삭제 + 익명 처리 → 유효 시 `SecurityContext` 세팅.
- 로그아웃: `SessionService.deleteSession(sid)` + `SessionCookieUtil.clearSidCookie()`.

### OAuth2 Login Rules (`CustomOauth2UserService`)
1. Email이 `loginType = "normal"`로 존재 → 차단 (인증 방식 혼용 방지)
2. Email이 동일 `loginType`으로 존재 → 로그인 허용
3. 신규 Email → `ROLE_USER`로 유저 생성

### Error Handling
`BusinessException(ErrorCode.XXX)` 또는 `BusinessException(ErrorCode.XXX, "custom message")`를 throw.
`GlobalExceptionHandler`가 캐치하여 아래 형식으로 반환:

| 상황 | HTTP | body `statusCode` | `success` |
|---|---|---|---|
| 성공 | 200 | 200 | true |
| 비즈니스/유효성 에러 | 200 | 201 | false |
| 잘못된 경로 | 404 | 404 | false |
| 서버 에러 | 500 | 502 | false |

### API 공통 응답 형식 (`ApiResponse<T>`)

```json
// 성공
{ "success": true, "message": "...", "statusCode": 200, "result": { ... } }

// 실패 (비즈니스/유효성)
{ "success": false, "message": "...", "statusCode": 201 }

// 잘못된 경로
{ "success": false, "message": "존재하지 않는 엔드포인트 입니다.", "statusCode": 404 }

// 서버 에러
{ "success": false, "message": "서버가 혼잡 하오니 잠시후 다시 시도해주세요...", "statusCode": 502 }
```

- `result`는 `null`이면 JSON에서 제외 (`@JsonInclude(NON_NULL)`)
- 모든 리스트 조회는 `limit`(기본 20), `offset` 쿼리 파라미터 지원

### Swagger 작성 규칙
- 컨트롤러에 Swagger 어노테이션 직접 작성 금지.
- `domain/{도메인}/docs/{도메인}ControllerDocs.java` 인터페이스에 작성.
- 컨트롤러는 해당 인터페이스를 `implements`하고 `@Override`만 추가.
- 접근 URL: `https://taro.neocompany.co.kr/api/swagger` (로컬: `http://localhost:8080/swagger`)

### Endpoint Security Tiers (`SecurityConfig`)
- **Public:** Swagger, OAuth2 경로, `/api/auth/login`, `/api/auth/signup`, `/api/auth/logout`
- **Authenticated (`ROLE_USER`):** `/api/auth/me`, `/api/auth/withdraw`, `/api/members/**`, `/api/orders/**`, `/api/payments/**`
- **Admin (`ROLE_ADMIN`):** `/api/admin/**`

### CORS (`CorsConfig`)
- **운영:** `https://taro.neocompany.co.kr` — credentials 허용
- **테스트:** `https://supretest.taro.neocompany.co.kr` — credentials 허용
- **로컬:** `http://localhost:5174`, `http://localhost:8080`
- **Toss PG 웹훅** (`/api/payments/webhooks/**`) — `*`, credentials 불가

## Database

- **DDL:** `spring.jpa.hibernate.ddl-auto=update` — 부팅 시 스키마 자동 반영
- **Timezone:** `Asia/Seoul`
- 모든 테이블 공통 컬럼: `idx` (PK, BIGINT UNSIGNED), `created_at`, `updated_at`
- `User.deleted` + `User.deletedAt` — 소프트 삭제 (탈퇴 시 email에 `deleted-` 접두사)

## DB 테이블 설계

| 테이블 | 주요 컬럼 |
|---|---|
| `user` | email(unique), password, nickname(unique, 10자), name, phone, age, birth, gender(male/female), social_type(kakao/google/naver), status(ready/active/stop/withdrawl_ready), is_auth, is_taro_master |
| `sessions` | session_id(unique), user_id(FK), expires_at, last_access_at |
| `taro_auth` | user_idx(FK), bank_address, bank_type, currency_exchange_rate(default 70) |
| `taro_payment_history` | payer_idx(FK), recipient_idx(FK), amount, type(taro/destiny), status, memo |
| `exchange_history` | user_idx(FK), settlement_fee, principal, profits, status |
| `room` | master_idx(FK), master_nickname, room_name, status(ready/full/active) |
| `room_price` | master_idx(FK), title, content(Text), price, discount_price(default 0) |

## Configuration Notes

- OAuth2 클라이언트 시크릿은 `application.yml`에 있음 (운영 시 환경변수로 분리 필요)
- Redis (`localhost:6379`), SMTP (Gmail) 설정 존재하나 현재 미사용
- Toss Payments 시크릿 키는 `application.yml`의 `toss.*` 항목
