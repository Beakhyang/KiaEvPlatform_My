관리자 (ADMIN_TBL)

번호 | 칼럼명 | 한글명 | 데이터 타입 | 크기 | Null허용 | 유일키(unique) | 키 | 비고
--- | --- | --- | --- | --- | --- | --- | --- | ---
1 | admin_no | 관리자번호 | BIGINT | 20 | N | Y | PK | AUTO_INCREMENT
2 | admin_id | 관리자아이디 | VARCHAR | 50 | N | Y | UK | 관리자 로그인 ID
3 | admin_pw | 관리자비밀번호 | VARCHAR | 255 | N | N |  | 현재 구현은 평문 비교, 추후 암호화 권장
4 | admin_name | 관리자명 | VARCHAR | 50 | N | N |  | 화면 표시명, 기본 GM
5 | admin_role | 관리자권한 | VARCHAR | 20 | N | N |  | SUPER_ADMIN 기본값
6 | admin_status | 관리자상태 | VARCHAR | 20 | N | N |  | ACTIVE / INACTIVE
7 | created_at | 등록일시 | DATETIME | - | N | N |  | 생성 시각
8 | updated_at | 수정일시 | DATETIME | - | Y | N |  | 수정 시각

챗봇 FAQ (CHATBOT_FAQ_TBL)

번호 | 칼럼명 | 한글명 | 데이터 타입 | 크기 | Null허용 | 유일키(unique) | 키 | 비고
--- | --- | --- | --- | --- | --- | --- | --- | ---
1 | faq_no | FAQ번호 | BIGINT | 20 | N | Y | PK | AUTO_INCREMENT
2 | category | 카테고리 | VARCHAR | 50 | N | N |  | 구매 상담, 가격/혜택 등
3 | question | 대표질문 | VARCHAR | 255 | N | N |  | 챗봇 버튼/추천질문 노출값
4 | answer | 답변내용 | VARCHAR | 4000 | N | N |  | 챗봇 응답 본문
5 | follow_up_questions | 후속질문목록 | VARCHAR | 1000 | Y | N |  | 줄바꿈 구분 문자열
6 | display_order | 노출순서 | INT | 11 | Y | N |  | 정렬 우선순위
7 | active_yn | 노출여부 | CHAR | 1 | N | N |  | Y/N
8 | created_at | 등록일시 | DATETIME | - | N | N |  | 생성 시각
9 | updated_at | 수정일시 | DATETIME | - | Y | N |  | 수정 시각

연동 메모

- `ADMIN_TBL.admin_no`는 `BOARD_TBL.admin_no`와 연결되어 공지 작성자 및 문의 답변 관리자 식별에 사용됩니다.
- `CHATBOT_FAQ_TBL`은 기존 사용자 FAQ 챗봇 화면과 직접 연결되며, 관리자 페이지에서 수정 즉시 반영됩니다.
- 프로모션 팝업 관리는 별도 팝업 테이블 대신 기존 `PROMOTION_TBL.type` 값을 `POPUP`으로 구분해 연동합니다.
