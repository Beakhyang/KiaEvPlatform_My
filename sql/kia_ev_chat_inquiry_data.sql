-- 기존에 테이블이 있다면 삭제 (초기화용)
DROP TABLE IF EXISTS chat_inquiry_tbl;

-- 챗봇 1:1 문의 테이블 생성
CREATE TABLE chat_inquiry_tbl (
    inquiry_no BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '문의번호',
    member_no BIGINT NULL COMMENT '회원번호 (비회원일 경우 NULL)',
    car_no BIGINT NULL COMMENT '차량번호 (관심 차량 선택 시)',
    category VARCHAR(50) NOT NULL COMMENT '문의유형 (예: 구매 상담, 가격/혜택, 충전)',
    writer_name VARCHAR(50) NOT NULL COMMENT '작성자명',
    writer_email VARCHAR(100) NOT NULL COMMENT '작성자이메일',
    content TEXT NOT NULL COMMENT '문의내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    is_answered CHAR(1) DEFAULT 'N' NOT NULL COMMENT '답변여부 (Y/N)',
    admin_no BIGINT NULL COMMENT '담당관리자번호',
    answer_content TEXT NULL COMMENT '답변내용',
    answered_at DATETIME NULL COMMENT '답변일시',
    chat_source VARCHAR(30) NULL COMMENT '문의유입경로 (예: FAQ, 추천질문, 직접입력)',
    question_summary VARCHAR(255) NULL COMMENT '사용자가 선택한 질문/요약',
    status VARCHAR(20) DEFAULT '접수' NULL COMMENT '처리상태 (예: 접수, 처리중, 완료)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='챗봇 1:1 문의 접수 테이블';	

-- [CASE 1] 비회원 / 챗봇에 직접 입력 / 아직 관리자 확인 전 (신규 접수)
INSERT INTO chat_inquiry_tbl 
(member_no, car_no, category, writer_name, writer_email, content, created_at, is_answered, chat_source, question_summary, status) 
VALUES 
(NULL, NULL, '구매 상담', '홍길동', 'hong@example.com', '안녕하세요, 처음 차량을 구매하려고 하는데 딜러 매칭은 어떻게 진행되나요?', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'N', '직접입력', '구매 절차 및 딜러 매칭 문의', '접수');

-- [CASE 2] 로그인 회원 / 추천 질문 클릭 / 답변 완료 상태
INSERT INTO chat_inquiry_tbl 
(member_no, car_no, category, writer_name, writer_email, content, created_at, is_answered, admin_no, answer_content, answered_at, chat_source, question_summary, status) 
VALUES 
(1001, NULL, '가격/혜택', '김철수', 'chulsoo@example.com', '이번 달 특별 프로모션 혜택이 어떻게 되나요?', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Y', 999, '안녕하세요 김철수 고객님. 이번 달은 취등록세 지원 및 보증기간 연장 프로모션이 진행 중입니다. 상세 내용은 이벤트 페이지를 참고해주세요.', DATE_SUB(NOW(), INTERVAL 1 DAY), '추천질문', '이달의 프로모션 혜택', '완료');

-- [CASE 3] 로그인 회원 / 특정 차량 지정 문의 / 관리자가 배정되어 처리 중인 상태
INSERT INTO chat_inquiry_tbl 
(member_no, car_no, category, writer_name, writer_email, content, created_at, is_answered, admin_no, chat_source, question_summary, status) 
VALUES 
(1002, 5055, '시승 예약', '이영희', 'younghee@example.com', '보고 있는 이 차량 이번 주말에 강남 전시장에서 시승 가능한가요?', DATE_SUB(NOW(), INTERVAL 5 HOUR), 'N', 998, 'FAQ', '주말 시승 예약 방법', '처리중');

-- [CASE 4] 비회원 / 전기차 충전 관련 / 추천 질문 클릭 / 답변 완료
INSERT INTO chat_inquiry_tbl 
(member_no, car_no, category, writer_name, writer_email, content, created_at, is_answered, admin_no, answer_content, answered_at, chat_source, question_summary, status) 
VALUES 
(NULL, NULL, '충전', '박지민', 'jimin@example.com', '전기차 충전 카드는 어떻게 발급받나요? 제휴 카드가 있나요?', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Y', 997, '전기차 충전 카드는 환경부 무공해차 통합누리집에서 회원가입 후 발급 신청이 가능하며, 당사 제휴 신용카드를 발급하시면 충전비 50% 할인 혜택이 있습니다.', DATE_SUB(NOW(), INTERVAL 3 DAY), '추천질문', '충전 카드 발급 및 제휴', '완료');

-- [CASE 5] 회원 / A/S 문의 / 챗봇에 직접 입력 / 방금 막 접수됨
INSERT INTO chat_inquiry_tbl 
(member_no, car_no, category, writer_name, writer_email, content, is_answered, chat_source, question_summary, status) 
VALUES 
(1005, 3012, 'A/S 및 정비', '최동훈', 'donghoon@example.com', '주행 중 약간의 소음이 들리는데, 근처 서비스센터 당일 점검 가능한가요?', 'N', '직접입력', 'A/S 당일 점검 가능 여부', '접수');

SELECT * 
FROM chat_inquiry_tbl;