-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: kiaevdb
-- ------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chatbot_faq_tbl`
--

DROP TABLE IF EXISTS `chatbot_faq_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chatbot_faq_tbl` (
  `faq_no` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL COMMENT 'FAQ 카테고리',
  `question` varchar(255) NOT NULL COMMENT '대표 질문',
  `answer` varchar(4000) NOT NULL COMMENT '챗봇 응답 내용',
  `follow_up_questions` varchar(1000) DEFAULT NULL COMMENT '후속 질문 목록(줄바꿈 구분)',
  `display_order` int DEFAULT '0' COMMENT '노출 순서',
  `active_yn` char(1) NOT NULL DEFAULT 'Y' COMMENT '노출 여부',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`faq_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chatbot_faq_tbl`
--

LOCK TABLES `chatbot_faq_tbl` WRITE;
/*!40000 ALTER TABLE `chatbot_faq_tbl` DISABLE KEYS */;
INSERT INTO `chatbot_faq_tbl`
  (`faq_no`, `category`, `question`, `answer`, `follow_up_questions`, `display_order`, `active_yn`, `created_at`, `updated_at`)
VALUES
  (1, '구매 상담', '차량 추천을 받고 싶어요',
   '주행거리와 예산 중심이면 EV3, 패밀리 SUV는 EV5 또는 EV9, 퍼포먼스와 장거리 주행을 함께 보신다면 EV6를 많이 찾으십니다. 용도를 남겨주시면 더 정확히 추천해드릴 수 있습니다.',
   'EV3 가격이 궁금해요\nEV6 주행거리가 궁금해요\n시승 상담을 신청하고 싶어요', 1, 'Y', NOW(), NOW()),
  (2, '구매 상담', '시승 상담을 신청하고 싶어요',
   '시승 및 구매 상담은 상담 신청 또는 1:1 문의로 접수해주시면 담당자가 순차적으로 연락드립니다. 희망 차종과 연락 가능한 시간을 함께 남겨주시면 더 빠르게 도와드릴 수 있습니다.',
   '차량 추천을 받고 싶어요\n출고 기간이 궁금해요\n보조금 안내를 받고 싶어요', 2, 'Y', NOW(), NOW()),
  (3, '가격/혜택', '보조금 안내를 받고 싶어요',
   '전기차 보조금은 차종과 거주 지역, 신청 시점에 따라 달라집니다. 정확한 금액은 최신 지자체 공고 기준으로 확인해야 하며, 원하시면 차종과 지역을 1:1 문의로 남겨주세요.',
   '가격이 궁금해요\n출고 기간이 궁금해요\n차량 추천을 받고 싶어요', 3, 'Y', NOW(), NOW()),
  (4, '가격/혜택', '가격이 궁금해요',
   '차량 가격은 트림과 옵션에 따라 달라집니다. 차량 목록 페이지에서 기본 가격을 확인하실 수 있고, 실제 구매 견적은 옵션과 보조금 반영 후 상담으로 안내받으시는 것이 가장 정확합니다.',
   '보조금 안내를 받고 싶어요\n시승 상담을 신청하고 싶어요\n차량 추천을 받고 싶어요', 4, 'Y', NOW(), NOW()),
  (5, '출고/계약', '출고 기간이 궁금해요',
   '출고 기간은 차종, 트림, 색상, 옵션, 재고 상황에 따라 달라집니다. 최신 일정은 상담 접수 후 확인하는 방식이 가장 정확합니다.',
   '차량 추천을 받고 싶어요\n보조금 안내를 받고 싶어요\n시승 상담을 신청하고 싶어요', 5, 'Y', NOW(), NOW()),
  (6, '충전', '충전 관련 문의를 하고 싶어요',
   '충전은 충전소 위치, 급속 충전 지원 여부, 차량별 배터리 스펙에 따라 경험이 달라집니다. 충전소 찾기 메뉴를 이용하시거나 차종을 남겨주시면 더 자세히 안내드릴 수 있습니다.',
   'EV6 주행거리가 궁금해요\n보조금 안내를 받고 싶어요\n차량 추천을 받고 싶어요', 6, 'Y', NOW(), NOW()),
  (7, '차량 안내', 'EV6 주행거리가 궁금해요',
   'EV6는 장거리 주행에 강점이 있는 대표 모델입니다. 세부 트림별 수치는 차량 상세 페이지에서 배터리와 주행가능거리를 함께 확인하실 수 있습니다.',
   '가격이 궁금해요\n충전 관련 문의를 하고 싶어요\n시승 상담을 신청하고 싶어요', 7, 'Y', NOW(), NOW()),
  (8, '차량 안내', 'EV3 가격이 궁금해요',
   'EV3 가격은 트림과 옵션에 따라 달라집니다. 차량 목록에서 기본 가격을 확인한 뒤, 보조금과 옵션을 반영한 실제 견적은 상담을 통해 받아보시는 것을 권장드립니다.',
   '보조금 안내를 받고 싶어요\n차량 추천을 받고 싶어요\n출고 기간이 궁금해요', 8, 'Y', NOW(), NOW());
/*!40000 ALTER TABLE `chatbot_faq_tbl` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
