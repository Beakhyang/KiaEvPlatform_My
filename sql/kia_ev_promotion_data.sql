-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: kiaevdb
-- ------------------------------------------------------
-- Server version 8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `promotion_tbl`
--

DROP TABLE IF EXISTS `promotion_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotion_tbl` (
  `promotion_id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL COMMENT '프로모션 유형(DISCOUNT, GIFT, EVENT)',
  `title` varchar(200) NOT NULL COMMENT '프로모션 제목',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '소제목 (짧은 설명)',
  `content` text COMMENT '상세 내용',
  `target_model` varchar(50) DEFAULT 'ALL' COMMENT '대상 차량 모델',
  `discount_amount` int DEFAULT '0' COMMENT '할인 금액',
  `start_date` datetime NOT NULL COMMENT '시작일',
  `end_date` datetime NOT NULL COMMENT '종료일',
  `banner_order` int DEFAULT '0' COMMENT '배너 노출 순서',
  `banner_image_url` varchar(255) DEFAULT NULL COMMENT '배너 이미지 경로',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '활성화 여부(1:Y, 0:N)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`promotion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_tbl`
-- 현재 날짜 기준으로도 데모 화면에서 '오늘 마감', '마감 임박', '인기 혜택'이 함께 보이도록
-- 상대 날짜를 사용했습니다.
--

LOCK TABLES `promotion_tbl` WRITE;
/*!40000 ALTER TABLE `promotion_tbl` DISABLE KEYS */;
INSERT INTO `promotion_tbl`
  (`promotion_id`, `type`, `title`, `sub_title`, `content`, `target_model`, `discount_amount`,
   `start_date`, `end_date`, `banner_order`, `banner_image_url`, `is_active`, `created_at`, `updated_at`)
VALUES
  (1, 'DISCOUNT', 'EV6 GT 라인 특별 지원', '압도적인 퍼포먼스, EV6를 더 가깝게',
   'EV6 GT 모델 한정 특별 혜택 제공', 'EV6', 1500000,
   DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1,
   '/images/promotion/ev6_spring.jpg', 1, NOW(), NOW()),
  (2, 'GIFT', 'Ray EV 도심 주행 패키지', '취등록세 지원부터 충전 크레딧까지!',
   '도심 주행에 최적화된 레이 EV 구매 시 혜택', 'Ray EV', 0,
   DATE_SUB(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 3,
   '/images/promotion/Ray_EV_도심주행패키지.jpg', 1, NOW(), NOW()),
  (3, 'DISCOUNT', 'EV9 패밀리 감사제', '최고의 플래그십 SUV, 특별 할인',
   '다자녀 가구를 위한 EV9 특별 할인', 'EV9', 3000000,
   DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 3,
   '/images/promotion/EV9_가족감사이벤트.jpg', 0, NOW(), NOW()),
  (4, 'EVENT', 'COMING SOON: EV3', '기아의 새로운 콤팩트 SUV, 가장 먼저 만나보세요',
   'EV3 사전 예약 알림 신청', 'EV3', 0,
   DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 4,
   '/images/promotion/니로_EV_재고_특별전_할인이벤트.jpg', 0, NOW(), NOW()),
  (5, 'DISCOUNT', '기아 EV 봄맞이 페스타', '전기차 전 모델 대상 스마트 할부 혜택',
   '봄 시즌 한정 스마트 할부 프로그램', 'ALL', 2000000,
   DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 16 DAY), 5,
   '/images/promotion/EV_봄맞이_페스타이벤트.jpg', 1, NOW(), NOW()),
  (6, 'GIFT', '2026 해맞이 EV 캠핑 세트', '전기차 계약 고객 대상 캠핑 의자 증정',
   '새해 맞이 V2L 활용 캠핑 용품 증정', 'ALL', 0,
   DATE_SUB(CURDATE(), INTERVAL 90 DAY), DATE_SUB(CURDATE(), INTERVAL 40 DAY), 6,
   '/uploads/니로_EV_재고_특별전_할인이벤트.jpg', 0, NOW(), NOW()),
  (7, 'DISCOUNT', '노후차 교체 특별 지원금', '10년 이상 노후차 폐차 후 EV 구매 시 추가 할인',
   '환경 보호를 위한 노후차 교체 캠페인', 'ALL', 300000,
   DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 40 DAY), 4,
   '/images/promotion/노후차_교체_특별_지원금_캠페인.jpg', 1, NOW(), NOW()),
  (8, 'GIFT', '기아 커넥트 스토어 이용권', '원격 주차, 스트리밍 서비스 1년 무료 체험',
   '기아 커넥트 스토어의 모든 기능을 1년간 무료로', 'ALL', 0,
   DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 5,
   '/uploads/기아_커넥트_스토어_1년_무료_이용권.jpg', 1, NOW(), NOW()),
  (9, 'DISCOUNT', '니로 EV 재고 특별전', '한정 수량! 바로 출고 가능한 니로 EV 특별 할인',
   '빠른 출고와 특별 할인을 동시에', 'Niro EV', 2000000,
   DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY) - INTERVAL 1 SECOND, 2,
   '/images/promotion/니로_EV_재고_특별전_할인이벤트.jpg', 1, NOW(), NOW()),
  (10, 'EVENT', '내부 테스트 프로모션', '시스템 테스트용 데이터입니다. (노출금지)',
   '관리자 테스트 목적의 데이터', 'ALL', 0,
   DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 10,
   '/uploads/니로_EV_재고_특별전_할인이벤트.jpg', 0, NOW(), NOW());
/*!40000 ALTER TABLE `promotion_tbl` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-14
