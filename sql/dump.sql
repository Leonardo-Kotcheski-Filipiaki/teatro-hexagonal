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
  KEY `fk_booking_event` (`event_id`),
  KEY `fk_booking_user` (`user_id`),
  CONSTRAINT `fk_booking_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (1,10,2,'CONFIRMED',NULL,'2026-06-08 20:10:39'),(2,10,2,'CONFIRMED',NULL,'2026-06-08 20:12:40');
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
INSERT INTO `booking_seat` VALUES (1,1),(1,2),(2,3),(2,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (6,2,'Teste de capacidade','2026-09-05 21:00:00',50,50,'ACTIVE','2026-06-08 18:19:34','2026-06-08 18:19:34'),(7,2,'Teste de redis','2026-09-05 21:00:00',80,80,'ACTIVE','2026-06-08 18:22:11','2026-06-08 18:22:11'),(8,2,'Teste de redis 2','2026-09-05 21:00:00',80,80,'ACTIVE','2026-06-08 18:22:28','2026-06-08 18:22:28'),(9,7,'Teste de redis 2','2026-09-05 21:00:00',80,80,'ACTIVE','2026-06-08 18:29:59','2026-06-08 18:29:59'),(10,7,'Teste de redis 2','2026-09-05 21:00:00',80,80,'ACTIVE','2026-06-08 20:10:12','2026-06-08 20:10:12');
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
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_seat_per_event` (`event_id`,`seat_code`),
  CONSTRAINT `fk_seat_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seat`
--

LOCK TABLES `seat` WRITE;
/*!40000 ALTER TABLE `seat` DISABLE KEYS */;
INSERT INTO `seat` VALUES (1,10,'A-1','R','2026-06-08 17:10:39',NULL),(2,10,'A-2','R','2026-06-08 17:10:39',NULL),(3,10,'A-3','R','2026-06-08 17:12:41',NULL),(4,10,'A-4','R','2026-06-08 17:12:41',NULL),(5,10,'A-5','D',NULL,NULL),(6,10,'A-6','D',NULL,NULL),(7,10,'A-7','D',NULL,NULL),(8,10,'A-8','D',NULL,NULL),(9,10,'B-1','D',NULL,NULL),(10,10,'B-2','D',NULL,NULL),(11,10,'B-3','D',NULL,NULL),(12,10,'B-4','D',NULL,NULL),(13,10,'B-5','D',NULL,NULL),(14,10,'B-6','D',NULL,NULL),(15,10,'B-7','D',NULL,NULL),(16,10,'B-8','D',NULL,NULL),(17,10,'C-1','D',NULL,NULL),(18,10,'C-2','D',NULL,NULL),(19,10,'C-3','D',NULL,NULL),(20,10,'C-4','D',NULL,NULL),(21,10,'C-5','D',NULL,NULL),(22,10,'C-6','D',NULL,NULL),(23,10,'C-7','D',NULL,NULL),(24,10,'C-8','D',NULL,NULL),(25,10,'D-1','D',NULL,NULL),(26,10,'D-2','D',NULL,NULL),(27,10,'D-3','D',NULL,NULL),(28,10,'D-4','D',NULL,NULL),(29,10,'D-5','D',NULL,NULL),(30,10,'D-6','D',NULL,NULL),(31,10,'D-7','D',NULL,NULL),(32,10,'D-8','D',NULL,NULL),(33,10,'E-1','D',NULL,NULL),(34,10,'E-2','D',NULL,NULL),(35,10,'E-3','D',NULL,NULL),(36,10,'E-4','D',NULL,NULL),(37,10,'E-5','D',NULL,NULL),(38,10,'E-6','D',NULL,NULL),(39,10,'E-7','D',NULL,NULL),(40,10,'E-8','D',NULL,NULL),(41,10,'F-1','D',NULL,NULL),(42,10,'F-2','D',NULL,NULL),(43,10,'F-3','D',NULL,NULL),(44,10,'F-4','D',NULL,NULL),(45,10,'F-5','D',NULL,NULL),(46,10,'F-6','D',NULL,NULL),(47,10,'F-7','D',NULL,NULL),(48,10,'F-8','D',NULL,NULL),(49,10,'G-1','D',NULL,NULL),(50,10,'G-2','D',NULL,NULL),(51,10,'G-3','D',NULL,NULL),(52,10,'G-4','D',NULL,NULL),(53,10,'G-5','D',NULL,NULL),(54,10,'G-6','D',NULL,NULL),(55,10,'G-7','D',NULL,NULL),(56,10,'G-8','D',NULL,NULL),(57,10,'H-1','D',NULL,NULL),(58,10,'H-2','D',NULL,NULL),(59,10,'H-3','D',NULL,NULL),(60,10,'H-4','D',NULL,NULL),(61,10,'H-5','D',NULL,NULL),(62,10,'H-6','D',NULL,NULL),(63,10,'H-7','D',NULL,NULL),(64,10,'H-8','D',NULL,NULL),(65,10,'I-1','D',NULL,NULL),(66,10,'I-2','D',NULL,NULL),(67,10,'I-3','D',NULL,NULL),(68,10,'I-4','D',NULL,NULL),(69,10,'I-5','D',NULL,NULL),(70,10,'I-6','D',NULL,NULL),(71,10,'I-7','D',NULL,NULL),(72,10,'I-8','D',NULL,NULL),(73,10,'J-1','D',NULL,NULL),(74,10,'J-2','D',NULL,NULL),(75,10,'J-3','D',NULL,NULL),(76,10,'J-4','D',NULL,NULL),(77,10,'J-5','D',NULL,NULL),(78,10,'J-6','D',NULL,NULL),(79,10,'J-7','D',NULL,NULL),(80,10,'J-8','D',NULL,NULL);
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
  `address` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `capacity` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theater`
--

LOCK TABLES `theater` WRITE;
/*!40000 ALTER TABLE `theater` DISABLE KEYS */;
INSERT INTO `theater` VALUES (1,'Teste','Endereço Teste','Cidade teste',50,'2026-06-05 00:01:48','2026-06-05 00:01:48'),(2,'Teste 2','Endereço Teste 2','Cidade teste 2',80,'2026-06-05 00:01:57','2026-06-05 00:01:57'),(7,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-05 02:10:37','2026-06-05 02:10:37'),(8,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-05 19:59:57','2026-06-05 19:59:57'),(9,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-08 18:48:53','2026-06-08 18:48:53'),(10,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-08 18:52:49','2026-06-08 18:52:49'),(11,'Luz e Sombra','Endereço Teste 2','Cidade teste 2',150,'2026-06-08 18:55:06','2026-06-08 18:55:06');
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
INSERT INTO `theater_capacity` VALUES (1,50),(2,80),(7,150),(8,150),(9,150),(10,150),(11,150);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'leonardo','leonardo@gmail.com','$2a$10$tHzIqt8ZelfQbm8FuusFwOhFelBGKt7i0/JlVlwpiFkGmx8cE7eZS','ADMIN',1,'2026-06-03 00:11:18','2026-06-03 00:11:18'),(2,'customer','customer@gmail.com','$2a$10$SEdBLjfZXMVhH1VoDSXefuQpAk7njYZ2.mIZC/TNX/.Grq2kE1NX.','CUSTOMER',1,'2026-06-03 00:16:29','2026-06-03 00:16:29'),(3,'leonardo2','leonardo2@gmail.com','$2a$10$i1TXOpjCKFbVtjndaPtF4uLaQFAXki01qY4E2T/PHp2QtA4mWURD6','ADMIN',1,'2026-06-05 19:59:12','2026-06-05 19:59:12'),(4,'customer2','customer2@gmail.com','$2a$10$Fb2Gg6Tn01mX8NPRIuh74uf0iUuIZ7eohomG8jLCKP9RdJft5iAQS','CUSTOMER',1,'2026-06-05 19:59:21','2026-06-05 19:59:21');
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

-- Dump completed on 2026-06-08 20:29:25
