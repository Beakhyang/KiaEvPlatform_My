USE kiaevdb;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS board_tbl;

CREATE TABLE `board_tbl` (
  `board_no` bigint NOT NULL AUTO_INCREMENT COMMENT '게시글번호 (PK)',
  `board_type` varchar(20) DEFAULT NULL COMMENT '게시글타입 (예: NOTICE, INQUIRY)',
  `member_no` bigint DEFAULT NULL COMMENT '회원번호 (문의글 작성자)',
  `admin_no` bigint DEFAULT NULL COMMENT '관리자번호 (공지 작성자 또는 답변자)',
  `title` varchar(2000) NOT NULL COMMENT '제목',
  `content` varchar(2000) NOT NULL COMMENT '내용',
  `is_pinned` varchar(1) DEFAULT NULL COMMENT '상단고정여부',
  `inquiry_status` varchar(20) DEFAULT NULL COMMENT '문의상태 (예: WAITING, COMPLETED)',
  `answer_content` varchar(2000) DEFAULT NULL COMMENT '답변내용',
  `answer_date` datetime DEFAULT NULL COMMENT '답변일시',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '조회수',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_yn` varchar(1) NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  `hidden` char(1) DEFAULT 'N' COMMENT '숨김여부',
  `notice_yn` varchar(255) DEFAULT 'N' COMMENT '공지여부',
  `priority` int DEFAULT '0' COMMENT '우선순위',
  PRIMARY KEY (`board_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. 공지사항 등록
-- admin_no 값은 실제 관리자 번호에 맞춰 조정 가능
-- ==========================================
INSERT INTO board_tbl (
    board_type,
    member_no,
    admin_no,
    title,
    content,
    is_pinned,
    inquiry_status,
    answer_content,
    answer_date,
    view_count,
    deleted_yn,
    hidden,
    notice_yn,
    priority
) VALUES
('NOTICE', NULL, 1, '[필독] 기아 EV 멤버십 서비스 이용 안내', '기아 EV 멤버십 서비스 이용 관련 주요 안내사항입니다. 충전, 정비, 이벤트 참여 등 주요 서비스는 로그인 후 더욱 편리하게 이용하실 수 있습니다.', 'Y', NULL, NULL, NULL, 128, 'N', 'N', 'Y', 100),
('NOTICE', NULL, 1, '2026년 상반기 전기차 정기 점검 및 소프트웨어 업데이트 안내', '2026년 상반기 기아 전기차 대상 정기 점검 및 소프트웨어 업데이트가 순차적으로 진행됩니다. 가까운 서비스 거점 또는 오토큐 예약 후 방문해 주세요.', 'Y', NULL, NULL, NULL, 86, 'N', 'N', 'Y', 90);

-- ==========================================
-- 2. 일반 회원 문의글 등록
-- 일반 사용자 게시글은 member_no = 5 로 통일
-- ==========================================
INSERT INTO board_tbl (
    board_type,
    member_no,
    admin_no,
    title,
    content,
    is_pinned,
    inquiry_status,
    answer_content,
    answer_date,
    view_count,
    deleted_yn,
    hidden,
    notice_yn,
    priority
) VALUES
('INQUIRY', 5, NULL, 'EV6 롱레인지 모델 실주행거리는 어느 정도 나오나요?', '출퇴근 위주로 하루 50km 정도 주행하고 있습니다. 실제 주행거리가 어느 정도 나오는지 궁금합니다.', 'N', 'WAITING', NULL, NULL, 14, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '가정용 완속 충전기 설치 시 필요한 절차가 궁금합니다', '아파트 거주 중인데 개인 완속 충전기 설치가 가능한지와 신청 절차를 알고 싶습니다.', 'N', 'WAITING', NULL, NULL, 9, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '기아 커넥트 앱에서 충전 시작 예약이 안 됩니다', '앱에서 예약 충전이 저장되지 않습니다. 같은 증상 해결하신 분 계실까요?', 'N', 'WAITING', NULL, NULL, 11, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '겨울철 배터리 효율이 많이 떨어지는 편인가요?', '히터를 켜면 주행 가능 거리가 얼마나 줄어드는지 궁금합니다.', 'N', 'WAITING', NULL, NULL, 7, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, 'EV9 2열 독립 시트 승차감 어떤가요?', '가족용 차량으로 보고 있는데 장거리 이동 시 2열 승차감이 궁금합니다.', 'N', 'WAITING', NULL, NULL, 6, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '공용 급속 충전소에서 충전이 중간에 자꾸 끊깁니다', '최근 외부 급속 충전소에서 80% 전후로 충전이 자주 종료됩니다.', 'N', 'WAITING', NULL, NULL, 13, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '전비 확인은 계기판이 정확한가요 앱이 정확한가요?', '계기판 전비와 앱 평균 전비가 조금 다르게 보여서 기준이 궁금합니다.', 'N', 'WAITING', NULL, NULL, 5, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '회생제동 i-PEDAL 설정하면 멀미가 심한 편인가요?', '가족이 처음 전기차를 타서 회생제동 강도를 어느 정도로 두는 게 좋을지 궁금합니다.', 'N', 'WAITING', NULL, NULL, 8, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '보조금 받고 구매한 차량을 중도 매도해도 되나요?', '전기차 보조금 관련 의무 운행 기간과 중고 판매 시 유의사항이 궁금합니다.', 'N', 'WAITING', NULL, NULL, 10, 'N', 'N', 'N', 0),
('INQUIRY', 5, NULL, '브레이크 밟을 때 저속에서 소리가 나는데 점검 받아야 할까요?', '주차장이나 저속 주행 중 브레이크 쪽에서 소리가 납니다. 흔한 증상인지 궁금합니다.', 'N', 'WAITING', NULL, NULL, 12, 'N', 'N', 'N', 0);

COMMIT;

SET SQL_SAFE_UPDATES = 1;

-- ==========================================
-- 3. 확인용 조회
-- board_type 은 DB에 영어 코드값으로 저장됨
-- 화면에서는 Thymeleaf에서 한글로 변환해서 표시
-- NOTICE -> 공지사항, INQUIRY -> 일반문의
-- ==========================================
SELECT board_no, board_type, member_no, admin_no, title, notice_yn, priority
FROM board_tbl
ORDER BY notice_yn DESC, priority DESC, board_no DESC;
