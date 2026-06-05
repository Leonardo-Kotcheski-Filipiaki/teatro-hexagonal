-- MySQL dump 10.13  Distrib 8.4.9, for Linux (x86_64)
--
-- Host: localhost    Database: theater_db
-- ------------------------------------------------------
-- Server version	8.4.9

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
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
  `booked_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_booking_event` (`event_id`),
  KEY `idx_booking_user` (`user_id`),
  CONSTRAINT `fk_booking_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (1,4,1,'CONFIRMED',NULL,'2026-06-05 00:12:11');
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_seat`
--

DROP TABLE IF EXISTS `booking_seat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_seat` (
  `booking_id` bigint NOT NULL,
  `seat_id` bigint NOT NULL,
  PRIMARY KEY (`booking_id`,`seat_id`),
  KEY `fk_bs_seat` (`seat_id`),
  CONSTRAINT `fk_bs_booking` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`),
  CONSTRAINT `fk_bs_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_seat`
--

LOCK TABLES `booking_seat` WRITE;
/*!40000 ALTER TABLE `booking_seat` DISABLE KEYS */;
INSERT INTO `booking_seat` VALUES (1,86),(1,87),(1,88),(1,89),(1,90);
/*!40000 ALTER TABLE `booking_seat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `theater_id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `event_date` timestamp NOT NULL,
  `total_seats` int NOT NULL,
  `available_seats` int NOT NULL,
  `status` enum('ACTIVE','CANCELLED','SOLD_OUT') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_event_theater` (`theater_id`),
  CONSTRAINT `fk_event_theater` FOREIGN KEY (`theater_id`) REFERENCES `theater` (`id`),
  CONSTRAINT `event_chk_1` CHECK ((`total_seats` <= 80))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (1,1,'Teste','2026-09-05 21:00:00',50,50,'ACTIVE','2026-06-05 00:02:19','2026-06-05 00:02:19'),(2,1,'Teste','2026-09-05 21:00:00',50,50,'ACTIVE','2026-06-05 00:02:47','2026-06-05 00:02:47'),(3,1,'Teste','2026-09-05 21:00:00',50,50,'ACTIVE','2026-06-05 00:05:36','2026-06-05 00:05:36'),(4,1,'Teste','2026-09-05 21:00:00',50,50,'ACTIVE','2026-06-05 00:10:17','2026-06-05 00:10:17');
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seat`
--

DROP TABLE IF EXISTS `seat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `seat_code` varchar(255) NOT NULL,
  `status` enum('D','M','R') NOT NULL DEFAULT 'D',
  `reserved_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_seat_per_event` (`event_id`,`seat_code`),
  KEY `idx_seat_event` (`event_id`),
  KEY `idx_seat_status` (`event_id`,`status`),
  CONSTRAINT `fk_seat_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seat`
--

LOCK TABLES `seat` WRITE;
/*!40000 ALTER TABLE `seat` DISABLE KEYS */;
INSERT INTO `seat` VALUES (1,1,'A-1','D',NULL),(2,1,'A-2','D',NULL),(3,1,'A-3','D',NULL),(4,1,'A-4','D',NULL),(5,1,'A-5','D',NULL),(6,1,'B-1','D',NULL),(7,1,'B-2','D',NULL),(8,1,'B-3','D',NULL),(9,1,'B-4','D',NULL),(10,1,'B-5','D',NULL),(11,1,'C-1','D',NULL),(12,1,'C-2','D',NULL),(13,1,'C-3','D',NULL),(14,1,'C-4','D',NULL),(15,1,'C-5','D',NULL),(16,1,'D-1','D',NULL),(17,1,'D-2','D',NULL),(18,1,'D-3','D',NULL),(19,1,'D-4','D',NULL),(20,1,'D-5','D',NULL),(21,1,'E-1','D',NULL),(22,1,'E-2','D',NULL),(23,1,'E-3','D',NULL),(24,1,'E-4','D',NULL),(25,1,'E-5','D',NULL),(26,1,'F-1','D',NULL),(27,1,'F-2','D',NULL),(28,1,'F-3','D',NULL),(29,1,'F-4','D',NULL),(30,1,'F-5','D',NULL),(31,1,'G-1','D',NULL),(32,1,'G-2','D',NULL),(33,1,'G-3','D',NULL),(34,1,'G-4','D',NULL),(35,1,'G-5','D',NULL),(36,1,'H-1','D',NULL),(37,1,'H-2','D',NULL),(38,1,'H-3','D',NULL),(39,1,'H-4','D',NULL),(40,1,'H-5','D',NULL),(41,1,'I-1','D',NULL),(42,1,'I-2','D',NULL),(43,1,'I-3','D',NULL),(44,1,'I-4','D',NULL),(45,1,'I-5','D',NULL),(46,4,'A-1','D',NULL),(47,4,'A-2','D',NULL),(48,4,'A-3','D',NULL),(49,4,'A-4','D',NULL),(50,4,'A-5','D',NULL),(51,4,'B-1','D',NULL),(52,4,'B-2','D',NULL),(53,4,'B-3','D',NULL),(54,4,'B-4','D',NULL),(55,4,'B-5','D',NULL),(56,4,'C-1','D',NULL),(57,4,'C-2','D',NULL),(58,4,'C-3','D',NULL),(59,4,'C-4','D',NULL),(60,4,'C-5','D',NULL),(61,4,'D-1','D',NULL),(62,4,'D-2','D',NULL),(63,4,'D-3','D',NULL),(64,4,'D-4','D',NULL),(65,4,'D-5','D',NULL),(66,4,'E-1','D',NULL),(67,4,'E-2','D',NULL),(68,4,'E-3','D',NULL),(69,4,'E-4','D',NULL),(70,4,'E-5','D',NULL),(71,4,'F-1','D',NULL),(72,4,'F-2','D',NULL),(73,4,'F-3','D',NULL),(74,4,'F-4','D',NULL),(75,4,'F-5','D',NULL),(76,4,'G-1','D',NULL),(77,4,'G-2','D',NULL),(78,4,'G-3','D',NULL),(79,4,'G-4','D',NULL),(80,4,'G-5','D',NULL),(81,4,'H-1','D',NULL),(82,4,'H-2','D',NULL),(83,4,'H-3','D',NULL),(84,4,'H-4','D',NULL),(85,4,'H-5','D',NULL),(86,4,'I-1','R','2026-06-04 21:12:11'),(87,4,'I-2','R','2026-06-04 21:12:11'),(88,4,'I-3','R','2026-06-04 21:12:11'),(89,4,'I-4','R','2026-06-04 21:12:11'),(90,4,'I-5','R','2026-06-04 21:12:11'),(91,4,'J-1','D',NULL),(92,4,'J-2','D',NULL),(93,4,'J-3','D',NULL),(94,4,'J-4','D',NULL),(95,4,'J-5','D',NULL);
/*!40000 ALTER TABLE `seat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `theater`
--

DROP TABLE IF EXISTS `theater`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `theater` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `capacity` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theater`
--

LOCK TABLES `theater` WRITE;
/*!40000 ALTER TABLE `theater` DISABLE KEYS */;
INSERT INTO `theater` VALUES (1,'Teste','Endereço Teste','Cidade teste',50,'2026-06-05 00:01:48','2026-06-05 00:01:48'),(2,'Teste 2','Endereço Teste 2','Cidade teste 2',80,'2026-06-05 00:01:57','2026-06-05 00:01:57'),(7,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-05 02:10:37','2026-06-05 02:10:37');
/*!40000 ALTER TABLE `theater` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `theater_capacity`
--

DROP TABLE IF EXISTS `theater_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `theater_capacity` (
  `theater_id` bigint NOT NULL,
  `capacity` int NOT NULL,
  PRIMARY KEY (`theater_id`),
  CONSTRAINT `fk_bs_theater` FOREIGN KEY (`theater_id`) REFERENCES `theater` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theater_capacity`
--

LOCK TABLES `theater_capacity` WRITE;
/*!40000 ALTER TABLE `theater_capacity` DISABLE KEYS */;
INSERT INTO `theater_capacity` VALUES (1,50),(2,80),(7,150);
/*!40000 ALTER TABLE `theater_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'leonardo','leonardo@gmail.com','$2a$10$tHzIqt8ZelfQbm8FuusFwOhFelBGKt7i0/JlVlwpiFkGmx8cE7eZS','ADMIN',1,'2026-06-03 00:11:18','2026-06-03 00:11:18'),(2,'customer','customer@gmail.com','$2a$10$SEdBLjfZXMVhH1VoDSXefuQpAk7njYZ2.mIZC/TNX/.Grq2kE1NX.','CUSTOMER',1,'2026-06-03 00:16:29','2026-06-03 00:16:29');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-05  2:26:48
