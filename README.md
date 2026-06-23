# DevLink Backend

> 개발자 취준생을 위한 커뮤니티 플랫폼 - 백엔드

<br />

## 📌 프로젝트 소개

**DevLink**는 개발자 취업을 준비하는 취준생들이 면접 후기, 스터디 모집, 기술 질문을 한 곳에서 나누고 함께 성장할 수 있는 커뮤니티 플랫폼입니다.

> 💡 직접 취준생으로서 필요성을 느끼고 기획한 서비스입니다.

<br />

## 👤 개발자 소개

| 이름 | 역할 | GitHub |
|------|------|--------|
| 추상현 | Frontend / Backend (1인 풀스택) | [chu723204-coder](https://github.com/chu723204-coder) |

<br />

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0-brightgreen?style=flat-square)
![MapStruct](https://img.shields.io/badge/MapStruct-1.5.5-red?style=flat-square)

### 인증
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-Kakao_Naver-FFCD00?style=flat-square)

### 실시간
![SSE](https://img.shields.io/badge/SSE-실시간알림-brightgreen?style=flat-square)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-brightgreen?style=flat-square)

### Database
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-DB호스팅-0B0D0E?style=flat-square&logo=railway&logoColor=white)

### API 문서
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)

<br />

## 📁 프로젝트 구조

```
src/main/java/com/devlink/
├── domain/
│   ├── auth/              # 인증 (로그인, JWT, OAuth)
│   ├── user/              # 회원
│   ├── post/              # 게시글 / 댓글
│   ├── study/             # 스터디 모집
│   ├── chat/              # 실시간 채팅
│   └── notification/      # 실시간 알림
└── global/
    ├── config/            # Security, WebSocket, QueryDSL, CORS 설정
    ├── jwt/               # JWT 토큰 발급 / 검증
    ├── oauth/             # 카카오 / 네이버 소셜 로그인
    ├── exception/         # 전역 예외 처리
    └── common/            # 공통 응답 포맷, BaseEntity
```

<br />

## ✨ 주요 기능

- **회원 관리** — 이메일 회원가입 / 카카오 · 네이버 소셜 로그인 / JWT 인증
- **게시판** — 자유게시판 / 면접 후기 / 기술 질문 / 취업 정보 CRUD (카테고리 필터 + 정렬)
- **스터디 모집** — 모집글 등록, 지원, 수락/거절 → 채팅방 자동 생성
- **실시간 알림** — 댓글, 좋아요, 스터디 지원/수락/거절 알림 (SSE)
- **실시간 채팅** — 스터디 팀원 간 채팅방 (WebSocket STOMP)
- **관리자 페이지** — 회원 정지/해제/탈퇴, 게시글 관리, 신고 처리/반려
- **신고 시스템** — 게시글·댓글 신고, 중복 신고 방지, 계정 정지 처리

<br />

## 🔐 인증 구조

| 토큰 | 저장소 | 유효기간 | 목적 |
|------|--------|----------|------|
| Access Token | Zustand 메모리 (프론트) | 30분 | XSS 공격 방지 |
| Refresh Token | HttpOnly Cookie | 7일 | 토큰 탈취 방지 |

- SecurityConfig: STATELESS 세션 정책, 401 커스텀 응답 (302 리다이렉트 방지)
- STOMP 연결 시 ChannelInterceptor로 JWT 헤더 검증
- ROLE_ADMIN 권한은 시드 데이터로 초기 발급, `@PreAuthorize`로 API 레벨 이중 검증

<br />

## 🔗 관련 링크

- **프론트 레포**: [DevLink_Front](https://github.com/chu723204-coder)

<br />

## ⚙️ 로컬 실행 방법

### 사전 준비
- Java 17
- Gradle
- PostgreSQL (또는 Railway DB 연결)

### 설치 및 실행

```bash
# 1. 레포지토리 클론
git clone https://github.com/chu723204-coder/DevLink_Back.git
cd DevLink_Back

# 2. 환경변수 설정
cp src/main/resources/application.example.properties src/main/resources/application.properties
# application.properties 열어서 값 입력

# 3. 빌드 및 실행
./gradlew bootRun
```

### 환경변수 설정 (application.properties)

```properties
# DB 설정
spring.datasource.url=your_railway_db_url
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# JWT
jwt.secret-key=your_jwt_secret_key
jwt.access-token-validity-in-milliseconds=1800000
jwt.refresh-token-validity-in-milliseconds=604800000

# Kakao OAuth
spring.security.oauth2.client.registration.kakao.client-id=your_kakao_client_id
spring.security.oauth2.client.registration.kakao.client-secret=your_kakao_client_secret

# Naver OAuth
spring.security.oauth2.client.registration.naver.client-id=your_naver_client_id
spring.security.oauth2.client.registration.naver.client-secret=your_naver_client_secret
```

<br />

## 📋 API 명세

| 도메인 | Method | URL | 설명 | 인증 |
|--------|--------|-----|------|------|
| 인증 | POST | /api/auth/signup | 회원가입 | ❌ |
| 인증 | POST | /api/auth/login | 로그인 | ❌ |
| 인증 | POST | /api/auth/oauth/kakao | 카카오 로그인 | ❌ |
| 인증 | POST | /api/auth/oauth/naver | 네이버 로그인 | ❌ |
| 회원 | GET | /api/users/me | 내 프로필 조회 | ✅ |
| 회원 | PUT | /api/users/me | 내 프로필 수정 | ✅ |
| 게시글 | GET | /api/posts | 게시글 목록 | ❌ |
| 게시글 | POST | /api/posts | 게시글 작성 | ✅ |
| 게시글 | GET | /api/posts/{id} | 게시글 상세 | ❌ |
| 게시글 | PUT | /api/posts/{id} | 게시글 수정 | ✅ |
| 게시글 | DELETE | /api/posts/{id} | 게시글 삭제 | ✅ |
| 스터디 | GET | /api/studies | 스터디 목록 | ❌ |
| 스터디 | POST | /api/studies | 모집글 등록 | ✅ |
| 스터디 | POST | /api/studies/{id}/apply | 지원하기 | ✅ |
| 알림 | GET | /api/notifications/subscribe | SSE 알림 구독 | ✅ |
| 알림 | GET | /api/notifications | 알림 목록 | ✅ |
| 채팅 | GET | /api/chat/rooms | 채팅방 목록 | ✅ |
| 관리자 | GET | /api/admin/users | 회원 목록 조회 | ✅ (ADMIN) |
| 관리자 | POST | /api/admin/users/{id}/ban | 회원 정지 | ✅ (ADMIN) |
| 관리자 | GET | /api/admin/reports | 신고 목록 조회 | ✅ (ADMIN) |

> 전체 API 문서는 로컬 실행 후 `http://localhost:8080/swagger-ui/index.html` 에서 확인하세요.

<br />

## 🌿 브랜치 전략

```
main          # 최종 브랜치
test_table    # 개발 통합 브랜치 (테스트/오류 확인)
feature/*     # 기능 개발 브랜치 (ex. feature/auth-login)
fix/*         # 버그 수정 브랜치 (ex. fix/notification-bug)
```

<br />

## 🛠 트러블슈팅

### 1. WebSocket 인증 401 오류
- **문제**: STOMP 메시지 전송 시 401 Unauthorized 에러 발생
- **원인**: SecurityConfig에서 `/ws/**` 경로를 차단하고 있었음
- **해결**: `/ws/**` permitAll 추가 + ChannelInterceptor로 STOMP 헤더에서 JWT 검증

### 2. STOMP UTF-8 인코딩 문제
- **문제**: 한글 채팅 메시지가 깨져서 수신됨
- **원인**: 기본 MessageConverter가 UTF-8 인코딩을 지원하지 않음
- **해결**: `MappingJackson2MessageConverter` 추가로 해결

### 3. 순환 참조 (Circular Dependency)
- **문제**: `chatService.ts`에서 `useAuthStore` import 시 순환 참조 오류
- **원인**: `api.ts` → `useAuthStore` → `chatService` 간 의존 순환
- **해결**: `useAuthStore.getState()`를 파라미터로 전달하는 방식으로 변경

<br />

## 📅 개발 기간

2026.06.08 ~ 2026.06.22

<br />

## 📄 라이선스

MIT License
