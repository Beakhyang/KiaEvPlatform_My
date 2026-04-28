# KIA EV Platform E2E 테스트

이 프로젝트는 Spring Boot 기반 KIA EV 플랫폼입니다. E2E 테스트는 Playwright로 실행하며, 기존 서비스 기능 코드는 수정하지 않고 브라우저 테스트/녹화 환경만 추가했습니다.

## 폴더 구조

- `e2e/playwright.config.ts`: 빠른 검증용 Playwright 설정
- `e2e/demo-v2.playwright.config.ts`: 시연 녹화용 Playwright 설정
- `e2e/tests/user.spec.ts`: 회원가입, 로그인, 마이페이지, 로그아웃 빠른 검증
- `e2e/demo-v2/user-demo.spec.ts`: 비회원, 회원 순서의 사용자 시연 녹화 테스트
- `e2e/support/db.ts`: `.env.e2e`의 DB 설정을 읽어 MySQL 연결
- `e2e/support/seed.ts`: 테스트 차량, 프로모션, 게시판, 충전소 데이터 입력
- `e2e/support/user.ts`: 회원가입/로그인 공통 함수
- `e2e/support/demo.ts`: 천천히 입력, 스크롤, 알럿 대기, 지도/챗봇 테스트 보조 함수
- `test-v2/artifacts/`: 시연 테스트 영상, 스크린샷, trace 저장 위치
- `test-v2/report/`: 시연 테스트 HTML 리포트
- `playwright-report/`: 빠른 검증 테스트 HTML 리포트

## 환경 변수

`.env.e2e.example`을 참고해 로컬에 `.env.e2e`를 만듭니다.

```env
DB_URL=jdbc:mysql://localhost:3306/kiaev_e2e?createDatabaseIfNotExist=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=root
DB_PASSWORD=
JPA_DDL_AUTO=create
PORT=8080
E2E_PORT=18080
CHARGING_SYNC_ENABLED=false
KAKAO_API_KEY=
GEMINI_API_KEY=
CHARGING_API_KEY=
```

E2E 실행 시 Spring Boot는 기본적으로 `E2E_PORT=8080`에서 실행됩니다. Kakao JavaScript SDK 도메인에 `http://localhost:8080`을 등록해 두면 실제 지도가 보입니다.

실제 Kakao 지도를 쓰는 것이 기본값입니다. Kakao 키가 없는 환경에서만 임시 화면으로 테스트하려면 `.env.e2e`에 `E2E_USE_MAP_MOCK=true`를 지정합니다.

## 실행 방법

최초 1회 브라우저를 설치합니다.

```bash
npm run e2e:install
```

빠른 사용자 검증 테스트:

```bash
npm run e2e:user
```

브라우저가 보이는 빠른 사용자 검증:

```bash
npm run e2e:user:headed
```

비회원, 회원 순서의 시연 녹화 테스트:

```bash
npm run e2e:demo:v2:headed
```

역할별 단독 실행:

```bash
npm run e2e:demo:v2:guest
npm run e2e:demo:v2:member
```

시연 리포트 열기:

```bash
npm run e2e:demo:v2:report
```

## 테스트 범위

비회원:
- 메인 화면 스크롤과 챗봇 질문 입력
- 차량 검색, 차량 상세, 썸네일 클릭, 비로그인 구매상담 알럿
- 문의 게시판 목록/상세/글쓰기 로그인 이동
- 프로모션 목록/상세 스크롤
- 충전소 검색, 마커 정보 확인, 현재 위치/현재 화면/전체 보기 버튼
- 챗봇 상담 페이지에서 구매상담, 추천 차량, 상담신청 로그인 이동

회원:
- 약관 동의, 회원가입, 로그인
- 게시글 작성, 상세, 수정, 삭제
- 차량 검색, 상세, 썸네일, 관심차량, 구매상담 신청
- 마이페이지, 상담내역 상세, 나의 문의내역 상세
- 프로모션, 충전소, 챗봇 상담, 메인 챗봇 질문

## 보안 주의사항

`.env.e2e`, `node_modules/`, `test-results/`, `playwright-report/`, `test-v*/`는 Git에 올리지 않습니다. DB 비밀번호, API Key, 토큰은 코드에 직접 작성하지 말고 `.env.e2e`에만 둡니다.

## 결과물 위치

- 시연 영상: `test-v2/artifacts/**/video.webm`
- 시연 스크린샷: `test-v2/artifacts/**/test-failed-*.png` 또는 테스트별 첨부 파일
- 시연 trace: `test-v2/artifacts/**/trace.zip`
- 시연 HTML 리포트: `test-v2/report/index.html`
- 빠른 검증 리포트: `playwright-report/index.html`

## 다음 보완 후보

- 관리자/딜러 로그인과 관리 화면 시연 테스트
- 모바일 viewport 녹화 테스트
- 실제 Kakao 지도 API 키가 있는 환경에서 지도 마커 클릭 검증
- 파일 업로드, 결제, 외부 API 실패 케이스 별도 시나리오
