# 타로 온라인 API 레퍼런스

> **Swagger UI**: `https://taro.neocompany.co.kr/api/swagger` (로컬: `http://localhost:8080/swagger`)
>
> REST API는 Swagger에서 직접 확인 가능합니다. **WebSocket/STOMP API는 이 문서에서만 확인**할 수 있습니다.

---

## 프론트엔드 필수 주의사항

### 1. 쿠키 인증 — `credentials` 설정 필수

모든 인증 요청에 SID 쿠키가 자동으로 전송되려면 HTTP 클라이언트에 반드시 설정해야 합니다.

```js
// fetch
fetch(url, { credentials: 'include' })

// axios (인스턴스 생성 시 전역 설정 권장)
axios.create({ baseURL: '...', withCredentials: true })
```

설정 없이는 로그인 후에도 인증이 필요한 모든 요청이 실패합니다.

---

### 2. 응답 처리 — HTTP 상태코드가 아닌 body로 판단

이 API는 비즈니스 에러도 **HTTP 200**으로 반환합니다. `res.ok` 또는 HTTP 상태코드만 보면 에러를 놓칩니다.

```js
const res = await fetch(url, { credentials: 'include' });
const body = await res.json();

if (!body.success) {
  // 실패 처리
  showError(body.message);
  return;
}

// 성공 처리
use(body.data);
```

| 상황 | HTTP 코드 | `body.success` | `body.statusCode` |
|---|---|---|---|
| 성공 | 200 | `true` | 200 |
| 비즈니스/유효성 실패 | 200 | `false` | 201 |
| 없는 경로 | 404 | `false` | 404 |
| 서버 오류 | 500 | `false` | 502 |

---

### 3. null 필드는 JSON key 자체가 없음

`null`인 필드는 응답 JSON에서 **키 자체가 제외**됩니다. 존재하지 않는 키를 읽으면 `undefined`이므로 옵셔널 체이닝으로 접근하세요.

```js
// 카드가 공개되지 않은 경우 cardName 키 자체가 없음
card.cardName        // undefined (에러는 아니지만 의도치 않은 동작 가능)
card?.cardName ?? '' // 안전한 접근
```

주요 `NON_NULL` 적용 필드:
- `CardReadingResponse.CardItem` — `isRevealed: false`인 카드는 `cardId`, `cardName`, `imageUrl` 등 미포함
- `TaroCardSetResponse.masterName` — 일반 조회 응답에서는 미포함 (Admin API에서만 포함)
- 모든 응답의 `data` 필드 — 데이터 없으면 키 자체 없음

---

### 4. 날짜/시간 형식

- 모든 datetime 필드는 **ISO 8601** 형식: `"2026-04-28T10:00:00Z"`
- 서버 타임존: **Asia/Seoul** (KST, UTC+9)
- 날짜 표시 시 로컬 변환 필요:

```js
new Date('2026-04-28T10:00:00Z').toLocaleString('ko-KR')
```

---

### 5. OAuth2 로그인 — 팝업 아닌 페이지 이동

OAuth2는 **팝업이 아닌 전체 페이지 이동** 방식입니다. 로그인 성공 후 SID 쿠키가 발급되며 설정된 redirect URI로 이동합니다.

```js
// 버튼 클릭 시 단순 이동 (팝업 X)
window.location.href = 'https://taro.neocompany.co.kr/api/oauth2/authorization/kakao';
```

| 제공자 | URL |
|---|---|
| 카카오 | `/oauth2/authorization/kakao` |
| 구글 | `/oauth2/authorization/google` |
| 네이버 | `/oauth2/authorization/naver` |

---

### 6. Toss 포인트 충전 플로우

```
1. POST /point/charge/toss/ready   → { orderId, chargeId, amount }
2. Toss SDK 결제창 열기 (orderId 전달)
3. 사용자 결제 완료
4. Toss → successUrl 리다이렉트 (paymentKey, orderId, amount 쿼리스트링 포함)
5. POST /point/charge/toss/confirm → { chargeId, paymentKey, orderId, amount }
```

`confirm`은 멱등성이 보장되므로 중복 호출해도 안전합니다.

---

### 7. STOMP 연결 및 구독 순서

```js
// 1. 반드시 로그인(SID 쿠키 발급) 후 연결
const socket = new SockJS('/ws/chat');
const client = Stomp.over(socket);

client.connect({}, () => {
  // 2. 연결 완료 콜백에서 subscribe 먼저
  client.subscribe(`/topic/rooms/${roomId}`, onEvent);
  client.subscribe(`/user/${myUserId}/queue/events`, onNotification);

  // 3. subscribe 완료 후 enter
  client.send(`/app/rooms/${roomId}/enter`, {}, '');
});

// 4. 재연결 처리 — 서버 재시작/네트워크 끊김 대비
client.reconnect_delay = 5000;
```

**subscribe 없이 enter를 먼저 보내면 이벤트를 받을 수 없습니다.**

---

### 8. 카드 리딩 STOMP 플로우

카드 이벤트는 순서가 있습니다. 순서를 벗어나면 서버에서 에러를 반환합니다.

```
마스터: cards/set    → 카드 세트 선택
마스터: cards/spread → 카드 펼치기 (뒤집힌 상태로 n장)
유저:   cards/pick   → 카드 선택 (position 지정)
마스터: cards/reveal → 카드 공개 (position 지정, 이때 카드 정보 포함)
마스터: cards/reset  → 리딩 초기화 (다시 처음부터)
```

`/topic/rooms/{roomId}` 구독으로 위 이벤트를 수신합니다. `type` 필드로 구분하세요.

---

### 9. 회원가입 순서

이메일 인증을 먼저 완료해야 가입이 가능합니다.

```
1. POST /auth/email/verification  → 이메일로 인증코드 발송
2. POST /auth/email/verify        → 인증코드 확인 (data: true 여야 다음 단계 진행)
3. POST /auth/signup              → 가입 완료
```

**비밀번호 규칙:** 영문자(대/소문자) + 숫자 각 1개 이상 포함, 최소 8자, 특수문자 허용

---

## 공통 사항

### Base URL

| 환경 | URL |
|---|---|
| 운영 | `https://taro.neocompany.co.kr/api` |
| 테스트 | `https://supretest.taro.neocompany.co.kr/api` |
| 로컬 | `http://localhost:8080/api` |

### 응답 형식

모든 REST 응답은 아래 형식으로 래핑됩니다.

```json
// 성공
{
  "success": true,
  "message": "처리 메시지",
  "statusCode": 200,
  "data": { }
}

// 비즈니스/유효성 실패 (HTTP 200)
{
  "success": false,
  "message": "실패 사유",
  "statusCode": 201
}

// 존재하지 않는 경로 (HTTP 404)
{
  "success": false,
  "message": "존재하지 않는 엔드포인트 입니다.",
  "statusCode": 404
}

// 서버 오류 (HTTP 500)
{
  "success": false,
  "message": "서버가 혼잡 하오니 잠시후 다시 시도해주세요...",
  "statusCode": 502
}
```

- `data` 필드는 값이 없을 때 JSON에서 제외됩니다.
- 목록 조회는 공통 페이지네이션 파라미터 `limit`(기본 20), `offset`(기본 0)을 지원합니다.

### 페이지네이션 응답 (`PageResult<T>`)

```json
{
  "items": [ ],
  "limit": 20,
  "offset": 0,
  "hasNext": false
}
```

---

## 인증 (SID 쿠키)

### 흐름

1. `POST /auth/login` 또는 OAuth2 로그인 → 서버가 `SID` HttpOnly 쿠키 발급 (1년 만료)
2. 이후 모든 인증 필요 요청에 브라우저가 자동으로 `SID` 쿠키를 전송

### Swagger에서 테스트하는 방법

1. `POST /auth/login` 실행 → 브라우저에 `SID` 쿠키 자동 저장
2. Swagger 우측 상단 **Authorize** 버튼 클릭
3. "SID Cookie" 란에 쿠키 값 입력 → **Authorize** 확인
4. 이후 잠금 아이콘이 있는 엔드포인트 테스트 가능

### OAuth2 로그인

아래 URL로 직접 이동하면 인증 후 `SID` 쿠키가 발급됩니다.

- 카카오: `/oauth2/authorization/kakao`
- 구글: `/oauth2/authorization/google`
- 네이버: `/oauth2/authorization/naver`

---

## REST API

### Auth (인증/회원)

**Base Path:** `/auth`

| 메서드 | 경로 | 인증 | 설명 | 요청 Body |
|---|---|---|---|---|
| POST | `/auth/login` | 없음 | 일반 로그인 (SID 쿠키 발급) | `{ email, password }` |
| POST | `/auth/signup` | 없음 | 회원가입 (이메일 인증 선행 필요) | `{ email, name, password, confirmPassword }` |
| POST | `/auth/duplications/email` | 없음 | 이메일 중복 확인 | `{ email }` |
| POST | `/auth/email/verification` | 없음 | 이메일 인증코드 발송 | `{ email }` |
| POST | `/auth/email/verify` | 없음 | 인증코드 확인 | `{ email, code }` |
| POST | `/auth/password/reset` | 없음 | 비밀번호 재설정 (이메일 인증 선행 필요) | `{ email, password, passwordConfirm, code }` |
| GET | `/auth/me` | **필요** | 내 정보 조회 | — |
| POST | `/auth/logout` | **필요** | 로그아웃 (SID 쿠키 만료) | — |
| POST | `/auth/withdraw` | **필요** | 회원탈퇴 | `{ password? }` (소셜 로그인은 생략 가능) |

**GET `/auth/me` 응답 예시:**
```json
{
  "email": "user@example.com",
  "loginType": "normal",
  "userName": "홍길동",
  "userRole": "ROLE_USER",
  "createdAt": "2026-01-01T00:00:00Z"
}
```

---

### TaroMaster (타로 마스터)

**Base Path:** `/taro-masters`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/taro-masters` | **필요** | 마스터 등록 신청 (승인 상태 `PENDING`) |
| GET | `/taro-masters` | 없음 | 승인된 공개 마스터 목록 (`?keyword=&status=&limit=&offset=`) |
| GET | `/taro-masters/me` | **필요** | 내 마스터 정보 조회 |
| GET | `/taro-masters/{masterId}` | 없음 | 마스터 상세 (비공개/미승인은 본인만 가능) |
| PATCH | `/taro-masters/me` | **필요** | 내 마스터 정보 수정 |

**POST `/taro-masters` 요청 Body:**
```json
{
  "displayName": "별빛타로",
  "intro": "10년 경력의 타로 마스터입니다.",
  "profileImageUrl": "https://...",
  "specialties": ["연애", "진로"],
  "careerYears": 10,
  "isPublic": true
}
```

**마스터 응답 구조:**
```json
{
  "masterId": 22,
  "userId": 10,
  "displayName": "별빛타로",
  "intro": "10년 경력의 타로 마스터입니다.",
  "profileImageUrl": "https://...",
  "specialties": ["연애", "진로"],
  "careerYears": 10,
  "status": "ONLINE",
  "approvalStatus": "APPROVED",
  "isPublic": true
}
```

- `status`: `ONLINE` | `BUSY` | `BREAK` | `OFFLINE`
- `approvalStatus`: `PENDING` | `APPROVED` | `REJECTED`

---

### TaroCardSet (타로 카드 세트)

**Base Path:** `/taro-card-sets`, `/taro-masters/me/card-sets`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/taro-card-sets` | 없음 | 공개 카드 세트 목록 (`?keyword=&masterId=&isActive=`) |
| GET | `/taro-card-sets/{setId}` | 없음 | 카드 세트 상세 (비공개는 본인 마스터만) |
| GET | `/taro-masters/me/card-sets` | **필요** | 내 카드 세트 목록 |
| POST | `/taro-masters/me/card-sets` | **필요** | 카드 세트 등록 |
| PATCH | `/taro-masters/me/card-sets/{setId}` | **필요** | 카드 세트 수정 |
| DELETE | `/taro-masters/me/card-sets/{setId}` | **필요** | 카드 세트 삭제 (소프트 삭제) |

**POST/PATCH 요청 Body:**
```json
{
  "setName": "라이더-웨이트 타로",
  "setDescription": "클래식 타로 덱입니다.",
  "brandName": "US Games",
  "publisherName": "US Games Systems",
  "coverImageUrl": "https://...",
  "cardCount": 78,
  "isPublic": true
}
```

---

### TaroCard (타로 카드)

**Base Path:** `/taro-cards`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/taro-cards/sets/{setId}` | 없음 | 세트별 카드 목록 (`?keyword=&isActive=`) |
| GET | `/taro-cards/{cardId}` | 없음 | 카드 상세 |
| POST | `/taro-cards` | **필요** (마스터) | 카드 등록 |
| PATCH | `/taro-cards/{cardId}` | **필요** (마스터) | 카드 수정 |
| DELETE | `/taro-cards/{cardId}` | **필요** (마스터) | 카드 삭제 (소프트 삭제) |

**POST/PATCH 요청 Body:**
```json
{
  "setId": 1,
  "cardName": "바보",
  "cardNumber": 0,
  "arcanaType": "MAJOR",
  "suit": null,
  "keywords": ["새로운 시작", "자유", "모험"],
  "cardDescription": "바보 카드는 새로운 여정의 시작을 상징합니다.",
  "uprightMeaning": "새로운 시작, 순수함, 자유",
  "reversedMeaning": "무모함, 부주의, 방황",
  "imageUrl": "https://...",
  "isActive": true
}
```

- `arcanaType`: `MAJOR` | `MINOR`
- `suit`: `WANDS` | `CUPS` | `SWORDS` | `PENTACLES` (MINOR 아르카나만 해당, MAJOR는 `null`)

---

### MasterPlan (상담 플랜)

**Base Path:** `/master-plans`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/master-plans` | 없음 | 공개 플랜 목록 (`?masterId=&isActive=`) |
| GET | `/master-plans/{planId}` | 없음 | 플랜 상세 |
| POST | `/master-plans` | **필요** (마스터) | 플랜 등록 |
| PATCH | `/master-plans/{planId}` | **필요** (마스터) | 플랜 수정 |
| DELETE | `/master-plans/{planId}` | **필요** (마스터) | 플랜 삭제 (소프트 삭제) |

**POST/PATCH 요청 Body:**
```json
{
  "planName": "기본 상담 플랜",
  "planDescription": "30분 기본 타로 상담입니다.",
  "counselingMinutes": 30,
  "price": 30000,
  "discountRate": 10,
  "isActive": true,
  "isPublic": true
}
```

---

### MasterAuth (마스터 인증/정산)

**Base Path:** `/master-auth`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/master-auth/settlement/me` | **필요** | 내 정산 계좌 조회 |
| POST | `/master-auth/settlement` | **필요** | 정산 계좌 등록 (최초 1회) |
| PATCH | `/master-auth/settlement` | **필요** | 정산 계좌 수정 |
| GET | `/master-auth/verification/me` | **필요** | 내 본인인증 상태 조회 |
| POST | `/master-auth/verification/pass` | **필요** | PASS 본인인증 처리 |

---

### Room (상담방)

**Base Path:** `/rooms`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/rooms` | **필요** (마스터) | 상담방 생성 (상태: `WAITING`) — body: `{ roomName, planId? }` |
| GET | `/rooms` | **필요** | 내가 참여한 상담방 목록 |
| GET | `/rooms/{roomId}` | **필요** (참여자) | 상담방 상세 + 참여자 목록 |
| PATCH | `/rooms/{roomId}` | **필요** (마스터) | 방 이름 수정 |
| DELETE | `/rooms/{roomId}` | **필요** (마스터) | 상담방 종료 (`CLOSED`) |
| GET | `/rooms/{roomId}/cards` | **필요** (참여자) | 현재 카드 리딩 현황 조회 |
| GET | `/rooms/{roomId}/payment` | **필요** (참여자) | 방의 결제 정보 조회 (금액/수수료/순액/상태) |

**방 상태 전이:**
```
WAITING → (STOMP start, 사전 결제 처리) → ACTIVE → (STOMP end) → CLOSED
WAITING → (STOMP close / DELETE HTTP) → CLOSED
```

> **사전 결제 게이트:** 마스터가 STOMP `start` 호출 시 사용자 포인트에서 `plan.discountedPrice`가 차감되고 마스터에게 적립됩니다. 잔액 부족 시 `INSUFFICIENT_POINTS` 반환되며 방은 WAITING 유지. `planId=null`인 방은 무료/테스트로 결제 건너뜀.

**GET `/rooms` 응답 아이템 구조:**
```json
{
  "id": 1,
  "masterId": 10,
  "masterName": "타로마스터홍길동",
  "roomName": "타로 상담실",
  "status": "ACTIVE",
  "lastMessage": "상담 요청드립니다.",
  "lastMessageAt": "2026-04-28T10:31:00Z",
  "unreadCount": 2,
  "createdAt": "2026-04-28T10:00:00Z"
}
```

**GET `/rooms/{roomId}` 응답 구조:**
```json
{
  "id": 1,
  "masterId": 10,
  "masterName": "타로마스터홍길동",
  "roomName": "타로 상담실",
  "status": "ACTIVE",
  "startedAt": "2026-04-28T10:05:00Z",
  "endedAt": null,
  "createdAt": "2026-04-28T10:00:00Z",
  "participants": [
    { "userId": 10, "userName": "타로마스터홍길동", "role": "MASTER", "isOnline": true },
    { "userId": 31, "userName": "강진호", "role": "USER", "isOnline": true }
  ]
}
```

**GET `/rooms/{roomId}/cards` 응답 구조:**
```json
{
  "cards": [
    {
      "position": 1,
      "isPicked": true,
      "isRevealed": true,
      "cardId": 5,
      "cardName": "황제",
      "arcanaType": "MAJOR",
      "suit": null,
      "keywords": ["권위", "안정", "성공"],
      "imageUrl": "https://...",
      "uprightMeaning": "성공과 권위, 안정된 기반",
      "reversedMeaning": "독재와 경직, 통제 상실"
    },
    {
      "position": 2,
      "isPicked": true,
      "isRevealed": false
    },
    {
      "position": 3,
      "isPicked": false,
      "isRevealed": false
    }
  ]
}
```

> `isRevealed=false`인 카드는 `position`, `isPicked`, `isRevealed`만 반환됩니다.

---

### WaitingRoom (대기열)

**Base Path:** `/waiting-room`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/waiting-room` | **없음** | 현재 대기 중인 상담방 목록 |

**응답 구조:**
```json
{
  "waitingCount": 3,
  "items": [
    {
      "queueNumber": 1,
      "roomId": 1,
      "masterId": 10,
      "masterName": "타로마스터홍길동",
      "roomName": "타로 상담실",
      "requestedAt": "2026-04-28T10:00:00Z"
    }
  ]
}
```

---

### Message (채팅 메시지)

**Base Path:** `/rooms/{roomId}/messages`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/rooms/{roomId}/messages` | **필요** (참여자) | 메시지 내역 (커서 기반 페이지네이션) |

**쿼리 파라미터:** `cursor` (메시지 ID, 최초 요청 시 생략), `size` (기본 20)

**응답 구조:**
```json
{
  "roomId": 1,
  "messages": [
    {
      "messageId": 100,
      "senderId": 31,
      "senderName": "강진호",
      "senderRole": "USER",
      "messageType": "TEXT",
      "content": "상담 요청드립니다.",
      "createdAt": "2026-04-28T10:31:00Z",
      "readCount": 1
    }
  ],
  "hasNext": true,
  "nextCursor": 50
}
```

> 최신 메시지부터 역순으로 반환됩니다. `nextCursor`를 다음 요청의 `cursor`에 전달하세요.

---

### Point (포인트 충전)

**Base Path:** `/point/charge`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/point/charge/toss/ready` | **필요** | 충전 준비 (Toss 결제창 오픈 전) |
| POST | `/point/charge/toss/confirm` | 없음 | 충전 확정 (Toss 콜백) |

**POST `/point/charge/toss/ready` 요청:**
```json
{ "amount": 10000 }
```

**응답:**
```json
{
  "chargeId": 1,
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 10000
}
```

**POST `/point/charge/toss/confirm` 요청:**
```json
{
  "chargeId": 1,
  "paymentKey": "tviva...",
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 10000
}
```

#### 사용자 지갑 / 내역 조회

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/point/wallet/me` | **필요** | 내 포인트 잔액 |
| GET | `/point/ledger/me` | **필요** | 포인트 변동 내역 (CHARGE/USE/REFUND) |

**`/point/wallet/me` 응답:**
```json
{ "userId": 31, "balance": 4500 }
```

**`/point/ledger/me` 응답 아이템:**
```json
{
  "id": 102,
  "delta": -5000,
  "balanceAfter": 4500,
  "type": "USE",
  "refTable": "room",
  "refId": 123,
  "createdAt": "2026-04-28T10:05:00Z"
}
```

---

### MasterEarning (마스터 적립금)

**Base Path:** `/master-auth/earnings`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/master-auth/earnings/me` | **필요** | 내 적립금 (잔액/누적적립/누적출금) |
| GET | `/master-auth/earnings/me/ledger` | **필요** | 적립 변동 내역 |

**`/earnings/me` 응답:**
```json
{
  "masterId": 22,
  "balance": 8000,
  "totalEarned": 12000,
  "totalWithdrawn": 4000
}
```

**`/earnings/me/ledger` 응답 아이템:**
```json
{
  "id": 50,
  "delta": 4000,
  "balanceAfter": 8000,
  "type": "EARN",
  "refTable": "room",
  "refId": 123,
  "createdAt": "2026-04-28T10:05:00Z"
}
```

`type`: `EARN` | `WITHDRAW` | `REFUND_DEDUCT` | `ADJUSTMENT`

---

### Withdrawal (마스터 출금)

**Base Path:** `/master-auth/withdrawals` (마스터), `/admin/withdrawals` (관리자)

#### 마스터 측

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/master-auth/withdrawals` | **필요** | 출금 신청 — body `{ amount }` |
| GET | `/master-auth/withdrawals` | **필요** | 내 신청 목록 |
| GET | `/master-auth/withdrawals/{id}` | **필요** | 내 신청 상세 |

**신청 선결 조건:**
- 마스터 등록 + 승인 완료
- 정산 계좌 등록 + `isVerifiedAccount = true`
- PASS 본인인증 완료
- 적립 잔액 ≥ 신청 금액

신청 즉시 잔액에서 해당 금액이 차감되어 잠금 처리됩니다. 거절 시 자동 복구.

#### 관리자 측 (`ROLE_ADMIN`)

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/admin/withdrawals?status=PENDING` | 전체 신청 목록 (status 필터) |
| GET | `/admin/withdrawals/{id}` | 신청 상세 |
| PATCH | `/admin/withdrawals/{id}/approve` | 승인 (외부 송금 직전 단계) |
| PATCH | `/admin/withdrawals/{id}/reject` | 거절 — body `{ reason }` |
| PATCH | `/admin/withdrawals/{id}/complete` | 외부 송금 완료 표시 |

**status:** `PENDING` → `APPROVED` → `COMPLETED` / `PENDING` → `REJECTED`

**응답 구조:**
```json
{
  "id": 7,
  "masterId": 22,
  "amount": 4000,
  "bankName": "국민은행",
  "accountNumber": "1234567890",
  "accountHolderName": "홍길동",
  "status": "PENDING",
  "processedByAdminId": null,
  "requestedAt": "2026-05-12T10:00:00Z",
  "processedAt": null,
  "rejectReason": null
}
```

> 계좌 정보는 **신청 시점 스냅샷** — 이후 정산 계좌 변경 영향 없음.

---

### Admin (관리자 전용)

> 모든 `/admin/**` 경로는 `ROLE_ADMIN` 필요. `/admin/auth/login` 제외.

#### Admin Auth

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/admin/auth/login` | 없음 | 관리자 로그인 |
| GET | `/admin/me` | **Admin** | 관리자 정보 조회 |

#### Admin TaroMaster

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/admin/taro-masters` | 전체 마스터 목록 (승인 상태 무관) |
| GET | `/admin/taro-masters/{masterId}` | 마스터 상세 |
| PATCH | `/admin/taro-masters/{masterId}/approval` | 마스터 승인/반려 |
| PATCH | `/admin/taro-masters/{masterId}/commission` | 수수료율 변경 — body `{ rate }` (0~100) |

**PATCH 요청 Body:**
```json
{
  "approvalStatus": "APPROVED",
  "reason": null
}
```

#### Admin ConsultationPayment

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/admin/consultation-payments/{paymentId}/refund` | 결제 환불 — 사용자 포인트 복구 + 마스터 적립 차감 |

> 마스터 적립 잔액 < 환불 금액인 경우 `MASTER_BALANCE_INSUFFICIENT_FOR_REFUND` 반환. 마스터가 이미 인출했다면 운영자가 별도 정산 필요.

#### Admin TaroCardSet

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/admin/taro-card-sets` | 전체 카드 세트 목록 (masterName 포함) |
| GET | `/admin/taro-card-sets/{setId}` | 카드 세트 상세 |
| GET | `/admin/taro-masters/me/card-sets` | 내 카드 세트 목록 |
| POST | `/admin/taro-masters/me/card-sets` | 카드 세트 등록 |
| PATCH | `/admin/taro-masters/me/card-sets/{setId}` | 카드 세트 수정 |
| DELETE | `/admin/taro-masters/me/card-sets/{setId}` | 카드 세트 삭제 |

#### Admin TaroCard

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/admin/taro-cards/sets/{setId}` | 세트별 카드 목록 |
| GET | `/admin/taro-cards/{cardId}` | 카드 상세 |
| POST | `/admin/taro-cards` | 카드 등록 |
| PATCH | `/admin/taro-cards/{cardId}` | 카드 수정 |
| DELETE | `/admin/taro-cards/{cardId}` | 카드 삭제 |

---

## STOMP WebSocket API

> Swagger에서 확인 불가능한 실시간 API입니다.

### 연결

| 항목 | 값 |
|---|---|
| 엔드포인트 | `/ws/chat` (SockJS 지원) |
| 인증 | HTTP 핸드셰이크 시 `SID` 쿠키로 자동 처리 |

**연결 전 REST 로그인으로 `SID` 쿠키를 먼저 발급받아야 합니다.**

```javascript
// SockJS + STOMP.js 예시
const socket = new SockJS('/ws/chat');
const client = Stomp.over(socket);
client.connect({}, () => { /* 연결 완료 */ });
```

---

### Subscribe (서버 → 클라이언트)

| Destination | 설명 |
|---|---|
| `/topic/rooms/{roomId}` | 방 이벤트 전체 (메시지, 카드, 방 상태 변경) |
| `/topic/waiting-room` | 대기열 변경 이벤트 |
| `/topic/masters/status` | 마스터 온라인 상태 변경 |
| `/user/{userId}/queue/events` | 개인 알림 (매칭 완료, 초대 등) |
| `/user/{userId}/queue/signaling` | WebRTC 시그널링 수신 |
| `/user/{userId}/queue/errors` | 개인 에러 알림 |

```javascript
// 방 이벤트 구독
client.subscribe(`/topic/rooms/${roomId}`, (msg) => {
  const event = JSON.parse(msg.body);
  console.log(event.type, event.data);
});

// 개인 알림 구독
client.subscribe(`/user/${myUserId}/queue/events`, (msg) => {
  const event = JSON.parse(msg.body);
});
```

---

### Send (클라이언트 → 서버)

#### 방 상태 명령

| Destination | 권한 | 설명 |
|---|---|---|
| `/app/rooms/{roomId}/enter` | 유저 | 상담방 입장 |
| `/app/rooms/{roomId}/leave` | 유저 | 상담방 퇴장 |
| `/app/rooms/{roomId}/start` | 마스터 | 상담 시작 (`WAITING` → `ACTIVE`) |
| `/app/rooms/{roomId}/end` | 마스터 | 상담 종료 (`ACTIVE` → `CLOSED`) |

```javascript
client.send(`/app/rooms/${roomId}/enter`, {}, '');
client.send(`/app/rooms/${roomId}/start`, {}, '');
```

#### 채팅

| Destination | 권한 | Body |
|---|---|---|
| `/app/rooms/{roomId}/messages` | 참여자 | `{ "content": "메시지 내용", "messageType": "TEXT" }` |
| `/app/rooms/{roomId}/read` | 참여자 | `{}` |
| `/app/rooms/{roomId}/typing` | 참여자 | `{ "isTyping": true }` |

#### 카드 리딩

| Destination | 권한 | Body | 설명 |
|---|---|---|---|
| `/app/rooms/{roomId}/cards/set` | 마스터 | `{ "setId": 1 }` | 카드 세트 선택 |
| `/app/rooms/{roomId}/cards/spread` | 마스터 | `{ "count": 3 }` | 카드 펼치기 |
| `/app/rooms/{roomId}/cards/pick` | 유저 | `{ "position": 1 }` | 카드 선택 |
| `/app/rooms/{roomId}/cards/reveal` | 마스터 | `{ "position": 1 }` | 카드 공개 |
| `/app/rooms/{roomId}/cards/reset` | 마스터 | `{}` | 카드 리딩 초기화 |

#### WebRTC 시그널링

| Destination | Body |
|---|---|
| `/app/rooms/{roomId}/signal/offer` | `{ "targetUserId": 31, "sdp": "..." }` |
| `/app/rooms/{roomId}/signal/answer` | `{ "targetUserId": 10, "sdp": "..." }` |
| `/app/rooms/{roomId}/signal/ice` | `{ "targetUserId": 31, "candidate": "..." }` |

#### 마스터 상태 변경

| Destination | Body |
|---|---|
| `/app/masters/status` | `{ "status": "ONLINE" }` |

- `status`: `ONLINE` | `BUSY` | `BREAK` | `OFFLINE`

---

### STOMP 이벤트 페이로드

`/topic/rooms/{roomId}` 구독 시 수신하는 이벤트 구조:

```json
{
  "type": "이벤트_타입",
  "data": { }
}
```

#### 채팅 이벤트

| `type` | 설명 | `data` 구조 |
|---|---|---|
| `MESSAGE` | 새 메시지 | `{ messageId, senderId, senderName, senderRole, messageType, content, createdAt, readCount }` |
| `READ` | 읽음 처리 | `{ userId, lastReadMessageId }` |
| `TYPING` | 타이핑 상태 | `{ userId, isTyping }` |

#### 방 상태 이벤트

| `type` | 설명 | `data` 구조 |
|---|---|---|
| `ROOM_STARTED` | 상담 시작 | `{ roomId, status: "ACTIVE", startedAt }` |
| `ROOM_ENDED` | 상담 종료 | `{ roomId, status: "CLOSED", endedAt }` |
| `USER_ENTERED` | 유저 입장 | `{ userId, userName, role }` |
| `USER_LEFT` | 유저 퇴장 | `{ userId }` |

#### 카드 리딩 이벤트

| `type` | 발신자 | 설명 |
|---|---|---|
| `CARD_SET_SELECTED` | 마스터 | 카드 세트 선택 완료 |
| `CARDS_SPREAD` | 마스터 | 카드 펼치기 완료 (뒤집힌 상태) |
| `CARD_PICKED` | 유저 | 카드 선택 |
| `CARD_REVEALED` | 마스터 | 카드 공개 (카드 정보 포함) |
| `READING_RESET` | 마스터 | 카드 리딩 초기화 |

**`CARD_REVEALED` data 예시:**
```json
{
  "position": 1,
  "isPicked": true,
  "isRevealed": true,
  "cardId": 5,
  "cardName": "황제",
  "arcanaType": "MAJOR",
  "suit": null,
  "keywords": ["권위", "안정"],
  "imageUrl": "https://...",
  "uprightMeaning": "성공과 권위",
  "reversedMeaning": "독재와 경직"
}
```

#### WebRTC 시그널링 (`/user/{userId}/queue/signaling`)

```json
{
  "type": "offer",
  "fromUserId": 10,
  "sdp": "..."
}
```

```json
{
  "type": "ice",
  "fromUserId": 10,
  "candidate": "..."
}
```

---

## 에러 코드 참조

모든 비즈니스 실패 응답은 HTTP 200 + `{ "success": false, "statusCode": 201 }` 형태입니다.

| 상황 | `message` 예시 |
|---|---|
| 이메일/비밀번호 불일치 | `"비밀번호가 일치하지 않습니다."` |
| 이메일 인증 미완료 | `"이메일 인증이 필요합니다."` |
| 인증 필요 (SID 없음) | `"인증이 필요합니다."` |
| 권한 없음 | `"접근 권한이 없습니다."` |
| 리소스 없음 | `"존재하지 않습니다."` |
| 마스터 미승인 | `"승인된 마스터만 방을 생성할 수 있습니다."` |
| 관리자 권한 없음 | `"관리자 권한이 없습니다."` |
