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
-- Table structure for table `sales_tbl`
--

DROP TABLE IF EXISTS `sales_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_tbl` (
  `sales_no` int NOT NULL AUTO_INCREMENT,
  `car_no` varchar(255) DEFAULT NULL,
  `model_name` varchar(100) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_tbl`
--

LOCK TABLES `sales_tbl` WRITE;
/*!40000 ALTER TABLE `sales_tbl` DISABLE KEYS */;
INSERT INTO `sales_tbl` VALUES (1,'234나2222','미분류 차량',20260402,20260402,1,60000000,'2026-04-02 12:03:21','2026-04-02 12:03:21','완료',1),(2,'345다3333','미분류 차량',20260403,20260403,1,50000000,'2026-04-02 12:03:41','2026-04-02 12:03:41','완료',9),(3,'123가1111',NULL,3,20260401,1,45000000,'2026-04-09 16:44:32','2026-04-09 16:44:31','COMPLETED',NULL);
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

-- Dump completed on 2026-04-09 16:52:09
