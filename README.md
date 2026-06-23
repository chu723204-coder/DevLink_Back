# DevLink Backend

> 개발자 취준생을 위한 커뮤니티 플랫폼 - 백엔드

<br />

## 📌 프로젝트 소개

**DevLink**는 개발자 취업을 준비하는 취준생들이 면접 후기, 스터디 모집, 기술 질문을 한 곳에서 나누고 함께 성장할 수 있는 커뮤니티 플랫폼입니다.

> 💡 직접 취준생으로서 필요성을 느끼고 기획한 서비스입니다.

<br />

## 👥 팀원 소개

| 이름 | 역할 | GitHub |
|------|------|--------|
| 팀원A | Frontend / Backend | [추상현](https://github.com/) |

<br />

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-3.5.14-6DB33F?style=flat-square&logo=springboot&logoColor=white)
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

### 배포
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=flat-square&logo=railway&logoColor=white)

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
- **게시판** — 자유게시판 / 면접 후기 / 기술 질문 / 취업 정보 CRUD
- **스터디 모집** — 모집글 등록, 지원, 수락/거절, 마감 처리
- **실시간 알림** — 댓글, 스터디 지원/수락 알림 (SSE)
- **실시간 채팅** — 스터디 팀원 간 채팅방 (WebSocket STOMP)
- **마이페이지** — 프로필 수정, 내 게시글, 스터디 내역

<br />

## 🔗 관련 링크

- **배포 링크**: [https://devlink-back.railway.app](https://devlink-back.railway.app)
- **프론트 레포**: [DevLink_Front](https://github.com/)
- **Swagger API 문서**: [https://devlink-back.railway.app/swagger-ui/index.html](https://devlink-back.railway.app/swagger-ui/index.html)

<br />

## ⚙️ 로컬 실행 방법

### 사전 준비
- Java 17
- Gradle
- PostgreSQL (또는 Railway DB 연결)

### 설치 및 실행

```bash
# 1. 레포지토리 클론
git clone https://github.com/your-repo/DevLink_Back.git
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

> 전체 API 문서는 Swagger에서 확인하세요.

<br />

## 🌿 브랜치 전략

```
main          # 최종 배포 브랜치
test_table    # 개발 통합 브랜치 (테스트/오류 확인)
feature/*     # 기능 개발 브랜치 (ex. feature/auth-login)
fix/*         # 버그 수정 브랜치 (ex. fix/notification-bug)
```

> PR 시 1명 이상 코드 리뷰 승인 필수

<br />

## 📅 개발 기간

2026.06.04 ~ 2026.06.30

<br />

## 📄 라이선스

MIT License
