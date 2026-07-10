# DevLink - 개발자 취준생 커뮤니티 플랫폼 (Backend)

> 개발자 취준생을 위한 실시간 커뮤니티 플랫폼 백엔드 서버

[![Frontend Repo](https://img.shields.io/badge/Frontend-Repository-blue?style=for-the-badge)](https://github.com/chu723204-coder/DevLink_Front)

---

## 📌 1. 프로젝트 소개

기존 취준생 커뮤니티(오픈채팅, 카페 등)는 면접 후기, 스터디 모집, 기술 질문 등의 정보가 분산되어 있어 한 곳에서 찾기 어려운 불편함을 직접 경험했습니다.

이를 해결하기 위해 **개발자 취준생 특화 커뮤니티 플랫폼**을 기획했으며, 단순 CRUD를 넘어 실시간 알림, 실시간 채팅, 소셜 로그인, 신고/관리자 시스템까지 실무에 가까운 완성도 높은 서비스를 목표로 개발했습니다.

---

## 🗓 2. 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 개발 기간 | 2026.06.08 ~ 2026.06.22 (약 2주) |
| 팀 규모 | 1인 풀스택 개발 |
| 담당 역할 | 기획 · 설계 · 백엔드 · 프론트엔드 전 과정 단독 개발 |

---

## 🛠 3. 기술 스택

### Backend
![Spring Boot](https://img.shields.io/badge/springboot-3.5-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA/Hibernate](https://img.shields.io/badge/jpa-hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/oauth2-4285F4?style=for-the-badge&logo=google&logoColor=white)

### Database
![PostgreSQL](https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

### Realtime
![WebSocket](https://img.shields.io/badge/websocket-STOMP-010101?style=for-the-badge)
![SSE](https://img.shields.io/badge/SSE-Server--Sent%20Events-orange?style=for-the-badge)

### Tools
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

---

## ⚙️ 4. 주요 기능

| 기능 | 설명 |
|------|------|
| 이메일 회원가입 | Gmail SMTP 이메일 인증 코드 발송 후 가입 |
| JWT 인증 | Access Token(메모리) + Refresh Token(HttpOnly Cookie) 듀얼토큰 |
| 소셜 로그인 | 카카오 / 네이버 OAuth2 연동 |
| 게시판 | 자유게시판 / 면접후기 / 기술질문 / 취업정보 CRUD |
| 스터디 모집 | 스터디 등록 / 지원 / 수락·거절 / 채팅방 자동 생성 |
| 실시간 채팅 | WebSocket STOMP 기반 스터디 팀 채팅 |
| 실시간 알림 | SSE 기반 댓글 / 좋아요 / 스터디 지원·수락·거절 알림 |
| 신고 시스템 | 게시글·댓글 신고 / 중복 신고 방지 |
| 관리자 페이지 | 회원 관리 / 게시글 관리 / 신고 처리 |

---

## 🗄 5. 시스템 구조

### ERD
> 이미지 추가 예정

### 데이터 흐름
```
클라이언트 요청
    → Spring Security Filter (JWT 검증)
    → Controller
    → Service
    → Repository
    → PostgreSQL DB
    → 응답
```

### API 문서
Swagger UI를 통해 전체 API 문서화 및 테스트 완료

- 로컬 실행 후 `http://localhost:8080/swagger-ui/index.html` 접속

---

## 🔍 6. 핵심 구현 내용

### JWT 듀얼토큰 설계

이전 ChargeNow 프로젝트에서 Access Token을 로컬스토리지에 저장했는데, 강사님으로부터 XSS 공격에 취약하다는 피드백을 받았습니다. 이를 개선하기 위해 DevLink에서는 아래와 같이 분리 설계했습니다.

| 토큰 | 저장 위치 | 이유 |
|------|----------|------|
| Access Token | Zustand 메모리 | XSS 공격 방지 |
| Refresh Token | HttpOnly Cookie | JS 접근 불가, 탈취 방지 |

- Access Token 유효기간: 30분 / 만료 시 자동 재발급
- Refresh Token 유효기간: 7일 / 로그아웃 시 서버에서 삭제

---

### SSE vs WebSocket 기술 선택

| 기술 | 통신 방향 | 적용 기능 | 선택 이유 |
|------|----------|----------|----------|
| SSE | 서버 → 클라이언트 단방향 | 실시간 알림 | 알림은 서버에서 클라이언트로만 전달하면 충분 |
| WebSocket STOMP | 양방향 | 실시간 채팅 | 채팅은 클라이언트↔서버 양방향 통신 필요 |

기능 요구사항을 분석해 각 기능에 맞는 기술을 선택했습니다.

---

### WebSocket JWT 인증

WebSocket은 HTTP와 달리 연결 이후 Spring Security 필터가 동작하지 않아 별도 인증 처리가 필요했습니다.

- STOMP 연결 헤더에 JWT 토큰 포함
- `ChannelInterceptor`에서 토큰 추출 후 유효성 검증
- 인증된 사용자만 채팅 참여 가능하도록 설계

---

## 🔧 7. 트러블슈팅

### ① WebSocket 인증 401 오류
- **문제**: STOMP 메시지 전송 시 401 Unauthorized 에러 발생
- **원인**: SecurityConfig에서 `/ws/**` 경로를 차단하고 있었음
- **해결**: `/ws/**` permitAll 추가 + ChannelInterceptor로 STOMP 헤더에서 JWT 검증

### ② 채팅 메시지 전송 안됨
- **문제**: 메시지 전송 후 채팅창에 아무것도 표시되지 않음
- **원인**: STOMP 연결/구독 설정이 제대로 되지 않았음
- **해결**: STOMP 연결 및 구독 설정 수정

### ③ 발신자/수신자 채팅 위치 동일
- **문제**: 내 메시지와 상대방 메시지가 같은 위치에 표시됨
- **원인**: isMine 구분 로직 미적용
- **해결**: isMine 조건 추가해 본인 메시지는 우측, 상대방은 좌측으로 구분

### ④ 메시지 2번씩 전송
- **문제**: 메시지 하나 보내면 두 개가 나타나는 현상
- **원인**: WebSocket 구독이 중복으로 등록됨
- **해결**: 구독 해제 처리 추가

### ⑤ 채팅 스크롤 버그
- **문제**: 새 메시지 수신 시 페이지 전체가 스크롤되는 현상
- **원인**: `scrollIntoView()` 사용으로 페이지 전체 스크롤 발생
- **해결**: `container.scrollTo()` 방식으로 변경해 채팅창 내부 스크롤만 동작

---

## 👤 8. 본인 기여

1인 풀스택 개발로 전 과정을 단독 진행했습니다.

- 서비스 기획 및 ERD 설계
- PostgreSQL 13개 테이블 설계
- Spring Boot 백엔드 전체 구현
- JWT 듀얼토큰 인증 체계 설계 및 구현
- OAuth2 카카오/네이버 소셜 로그인 구현
- WebSocket STOMP 실시간 채팅 구현
- SSE 실시간 알림 구현
- 신고 시스템 및 관리자 페이지 구현
- React + TypeScript 프론트엔드 전체 구현

---

## 🤝 9. 협업 방식

1인 개발 프로젝트로 아래 Git 전략을 적용했습니다.

- `main`: 배포용 브랜치
- `develop`: 개발 통합 브랜치
- `feature/기능명`: 기능별 브랜치 분리

---

## 💬 10. 회고

### 잘된 점
- SSE + WebSocket STOMP 실시간 기능 완성
- JWT 이중 인증 구조 설계 및 구현
- 신고 / 관리자 시스템으로 서비스 완성도 향상
- 1인 개발로 기획부터 구현까지 전 과정 단독 진행

### 아쉬운 점
- 배포 미완성 (로컬 실행 단계)
- 댓글 좋아요 / 대댓글 미구현
- 이미지 업로드 기능 미구현
- 테스트 코드 부재

### 향후 계획
- Railway + Vercel 배포 완성
- 검색 기능 추가
- 이미지 업로드 (S3 또는 Cloudinary)
- JUnit 단위 테스트 작성
