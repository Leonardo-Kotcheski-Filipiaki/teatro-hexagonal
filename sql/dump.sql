mysqldump: [Warning] Using a password on the command line interface can be insecure.
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (1,1,'O Fantasma da Ópera - Estreia Nacional','2026-07-15 20:30:00',80,80,'ACTIVE','2026-06-04 01:49:11','2026-06-04 01:49:11'),(2,2,'Cronos - Tour de Despedida (Rock Nacional)','2026-09-05 21:00:00',60,60,'ACTIVE','2026-06-04 02:12:15','2026-06-04 02:12:15');
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
  `seat_code` varchar(10) NOT NULL,
  `status` enum('D','M','R') NOT NULL DEFAULT 'D',
  `reserved_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_seat_per_event` (`event_id`,`seat_code`),
  KEY `idx_seat_event` (`event_id`),
  KEY `idx_seat_status` (`event_id`,`status`),
  CONSTRAINT `fk_seat_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seat`
--

LOCK TABLES `seat` WRITE;
/*!40000 ALTER TABLE `seat` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theater`
--

LOCK TABLES `theater` WRITE;
/*!40000 ALTER TABLE `theater` DISABLE KEYS */;
INSERT INTO `theater` VALUES (1,'Teste','Endereço Teste','Cidade teste',80,'2026-06-03 00:58:29','2026-06-03 00:58:29'),(2,'Teste 2','Endereço Teste 2','Cidade teste 2',50,'2026-06-03 00:58:38','2026-06-03 00:58:38');
/*!40000 ALTER TABLE `theater` ENABLE KEYS */;
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

-- Dump completed on 2026-06-04  3:09:17
