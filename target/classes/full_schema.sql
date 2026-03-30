-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: inventory_db
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `dealers`
--

DROP TABLE IF EXISTS `dealers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dealers` (
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `address` longtext,
  `contact_person` varchar(255) NOT NULL,
  `email` longtext NOT NULL,
  `name` varchar(255) NOT NULL,
  `subscription_type` enum('BASIC','PREMIUM','ENTERPRISE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_dealer_tenant` (`tenant_id`),
  KEY `idx_dealer_subscription` (`subscription_type`),
  KEY `idx_dealer_active` (`active`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `quantity` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `dealer_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `last_updated` datetime(6) DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `vehicle_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_inventory_vehicle` (`vehicle_id`),
  KEY `idx_inventory_dealer` (`dealer_id`),
  KEY `idx_inventory_tenant` (`tenant_id`),
  CONSTRAINT `FK76bahvmvk20vpxun81m2ls6pu` FOREIGN KEY (`dealer_id`) REFERENCES `dealers` (`id`),
  CONSTRAINT `FK7ns3vrp565d443nw903287dbv` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `quantity` int NOT NULL,
  `sub_total` decimal(38,2) NOT NULL,
  `unit_price` decimal(38,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `vehicle_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_vehicle` (`vehicle_id`),
  KEY `idx_order_item_tenant` (`tenant_id`),
  CONSTRAINT `FK9un7bv0yifhn1hpkh5ioechp7` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `FKpwrjm3jjj3nrygejitsdpokta` FOREIGN KEY (`order_id`) REFERENCES `vehicle_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` enum('USER','ADMIN','GLOBAL_ADMIN','DEALER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tenants`
--

DROP TABLE IF EXISTS `tenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenants` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_aye4nalvpsbxpjq0vt2ulaiqi` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKdms6tsnbugy9927cn4kwci01a` (`email`,`tenant_id`),
  KEY `idx_email` (`email`),
  KEY `idx_user_tenant_id` (`tenant_id`),
  KEY `idx_user_role` (`role_id`),
  KEY `idx_email_tenant` (`email`,`tenant_id`),
  KEY `idx_user_active` (`active`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vehicle_orders`
--

DROP TABLE IF EXISTS `vehicle_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `order_number` varchar(255) NOT NULL,
  `status` enum('PENDING','PROCESSING','PAID','COMPLETED','CANCELLED','RETURNED') NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `dealer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9q84rytvf7ppmipaslq6lj210` (`order_number`),
  KEY `idx_order_dealer` (`dealer_id`),
  KEY `idx_order_status` (`status`),
  KEY `idx_order_tenant` (`tenant_id`),
  CONSTRAINT `FKf658eijmlp3uex4ls3aqs5dft` FOREIGN KEY (`dealer_id`) REFERENCES `dealers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicles` (
  `available` bit(1) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `dealer_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `year` varchar(10) DEFAULT NULL,
  `manufacturer` varchar(50) DEFAULT NULL,
  `vin` varchar(50) DEFAULT NULL,
  `model` varchar(100) NOT NULL,
  `description` longtext,
  `status` enum('AVAILABLE','SOLD','UNDER_MAINTENANCE','RESERVED') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_tenant_status` (`tenant_id`,`status`),
  KEY `idx_vehicle_dealer_tenant` (`dealer_id`,`tenant_id`),
  KEY `idx_vehicle_model_tenant` (`model`,`tenant_id`),
  KEY `idx_vehicle_status` (`status`),
  KEY `idx_vehicle_vin` (`vin`),
  CONSTRAINT `FKeoeyjqpoexahywjp6k7m6d9bi` FOREIGN KEY (`dealer_id`) REFERENCES `dealers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-31  2:59:25
