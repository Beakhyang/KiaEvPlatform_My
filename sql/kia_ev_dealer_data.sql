-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: kiaevdb
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `dealer_tbl`
--

DROP TABLE IF EXISTS `dealer_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dealer_tbl` (
  `dealer_no` int NOT NULL AUTO_INCREMENT,
  `dealer_emp_no` varchar(30) NOT NULL,
  `dealer_pw` varchar(255) NOT NULL,
  `dealer_name` varchar(50) NOT NULL,
  `birth_date` date NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `affiliation` varchar(100) NOT NULL,
  `dealer_status` varchar(20) DEFAULT NULL,
  `approval_status` varchar(20) NOT NULL,
  `account_role` varchar(20) NOT NULL DEFAULT 'DEALER' COMMENT '계정 역할 (ADMIN / DEALER)',
  `account_status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '계정 상태 (ACTIVE / LEAVE / RETIRED)',
  `hire_date` date DEFAULT NULL COMMENT '입사일',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`dealer_no`),
  UNIQUE KEY `uk_dealer_no` (`dealer_no`),
  UNIQUE KEY `uk_dealer_emp_no` (`dealer_emp_no`),
  CONSTRAINT `chk_affiliation` CHECK ((`affiliation` in (_utf8mb4'서울본점',_utf8mb4'인천지점',_utf8mb4'대구지점',_utf8mb4'부산지점',_utf8mb4'제주지점'))),
  CONSTRAINT `chk_approval_status` CHECK ((`approval_status` in (_utf8mb4'WAIT',_utf8mb4'APPROVED',_utf8mb4'REJECTED'))),
  CONSTRAINT `chk_dealer_status` CHECK ((`dealer_status` in (_utf8mb4'ACTIVE',_utf8mb4'INACTIVE')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dealer_tbl`
--

LOCK TABLES `dealer_tbl` WRITE;
/*!40000 ALTER TABLE `dealer_tbl` DISABLE KEYS */;
INSERT INTO `dealer_tbl` VALUES (1,'202604D001','1234','김도현','1988-05-21','010-4101-3101','dealer01@kiaev.com','서울본점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-14 17:34:12'),(2,'202604D002','1234','이서준','1990-09-12','010-4202-3202','dealer02@kiaev.com','인천지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(3,'202604D003','1234','박지훈','1987-11-04','010-4303-3303','dealer03@kiaev.com','대구지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(4,'202604D004','1234','최유진','1991-01-29','010-4404-3404','dealer04@kiaev.com','부산지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(5,'202604D005','1234','정은우','1986-07-17','010-4505-3505','dealer05@kiaev.com','제주지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(6,'202604D006','1234','한소민','1992-03-08','010-4606-3606','dealer06@kiaev.com','서울본점','INACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(7,'202604D007','1234','윤태성','1989-12-25','010-4707-3707','dealer07@kiaev.com','인천지점','ACTIVE','WAIT','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(8,'202604D008','1234','강수아','1993-06-30','010-4808-3808','dealer08@kiaev.com','대구지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(9,'202604D009','1234','신재민','1985-10-14','010-4909-3909','dealer09@kiaev.com','부산지점','INACTIVE','REJECTED','DEALER','RETIRED',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13'),(10,'202604D010','1234','오세린','1994-04-19','010-5010-4010','dealer10@kiaev.com','제주지점','ACTIVE','APPROVED','DEALER','ACTIVE',NULL,'2026-04-09 19:49:13','2026-04-09 19:49:13');
/*!40000 ALTER TABLE `dealer_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales_tbl`
--

DROP TABLE IF EXISTS `sales_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_tbl` (
  `sales_no` int NOT NULL AUTO_INCREMENT,
  `car_no` varchar(255) DEFAULT NULL,
  `model_name` varchar(255) DEFAULT NULL,
  `consult_no` int NOT NULL,
  `member_no` int NOT NULL,
  `dealer_no` int NOT NULL,
  `sales_amount` int NOT NULL,
  `sales_date` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sales_status` varchar(255) DEFAULT NULL,
  `car_model_no` int DEFAULT NULL,
  PRIMARY KEY (`sales_no`),
  UNIQUE KEY `uq_sales_consult_no` (`consult_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_tbl`
--

LOCK TABLES `sales_tbl` WRITE;
/*!40000 ALTER TABLE `sales_tbl` DISABLE KEYS */;
INSERT INTO `sales_tbl` VALUES (1,'9',NULL,13,20260403,1,47000000,'2026-04-13 12:30:22','2026-04-13 12:30:21','COMPLETED',9),(2,'7',NULL,14,20260404,1,46000000,'2026-04-13 14:17:51','2026-04-13 14:17:51','COMPLETED',7);
/*!40000 ALTER TABLE `sales_tbl` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-15 17:20:51
