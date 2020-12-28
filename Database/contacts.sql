-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: contacts
-- ------------------------------------------------------
-- Server version	8.0.21

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
-- Table structure for table `groupcontacts`
--

DROP TABLE IF EXISTS `groupcontacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `groupcontacts` (
  `GroupID` int NOT NULL AUTO_INCREMENT,
  `NameGroup` text,
  PRIMARY KEY (`GroupID`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groupcontacts`
--

LOCK TABLES `groupcontacts` WRITE;
/*!40000 ALTER TABLE `groupcontacts` DISABLE KEYS */;
INSERT INTO `groupcontacts` VALUES (80,'Bạn bè'),(81,'Đồng Nghiệp'),(82,'Người thân'),(83,'Đồng đội');
/*!40000 ALTER TABLE `groupcontacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person` (
  `PersonID` int NOT NULL AUTO_INCREMENT,
  `FullName` text,
  `PhoneNumber` text,
  `Address` text,
  `Email` text,
  `GroupID` int DEFAULT NULL,
  PRIMARY KEY (`PersonID`),
  KEY `GroupID` (`GroupID`),
  CONSTRAINT `person_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groupcontacts` (`GroupID`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (35,'Long Nhật','0393134221','Quảng Trị','longnhat@gmail.com',80),(36,'Minh Đức','0822828128','Đà Nẵng','minhduc@gmail.com',80),(39,'Lê Thị B','0822626163','Nghệ An','lethingoc@gmail.com',83),(64,'Đức Minh','0372717711','Huế','ducminh@gmail.com',80),(65,'Huyền Nhi','0928826224','Đà Nẵng','huyennhi@gmail.com',80),(66,'Lê Mạnh','0339123493','Nghệ An','lemanh@gmail.com',80),(67,'Lê Huyền','0822828129','Đà Nẵng','lehuyenh@gmail.com',81),(68,'Lê Thị Ngọc','0822626162','Nghệ An','lethingoc@gmail.com',82),(72,'le','0339134073','lexuanman','lexuanmanh101199@gmail.com',80);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-28 12:51:51
