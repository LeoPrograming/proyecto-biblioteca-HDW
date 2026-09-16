CREATE DATABASE  IF NOT EXISTS `stm_biblioteca` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `stm_biblioteca`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: stm_biblioteca
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `autor`
--

DROP TABLE IF EXISTS `autor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `autor` (
  `id_autor` int(11) NOT NULL AUTO_INCREMENT,
  `nombres` varchar(120) NOT NULL,
  `apellidos` varchar(120) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_autor`),
  UNIQUE KEY `unq_autor` (`nombres`,`apellidos`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autor`
--

LOCK TABLES `autor` WRITE;
/*!40000 ALTER TABLE `autor` DISABLE KEYS */;
INSERT INTO `autor` VALUES (1,'J. R. R.','Tolkien',1),(2,'J. K.','Rowling',1),(3,'Frank','Herbert',1),(4,'Isaac','Asimov',1),(5,'Umberto','Eco',1),(6,'Agatha','Christie',1);
/*!40000 ALTER TABLE `autor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `imagen` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Fantasia','Mundos magicos y aventuras',1,'https://images.unsplash.com/photo-1519682337058-a94d519337bc'),(2,'Ciencia Ficcion','Tecnologia y futuros posibles',1,'https://images.unsplash.com/photo-1519682337058-a94d519337bc'),(3,'Misterio','Intrigas y detectives',1,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(4,'Programacion','Libros relacionados a desarrollo de software II',1,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(5,'Cálculo','Introducción al Cálculo I',1,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cola_espera`
--

DROP TABLE IF EXISTS `cola_espera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cola_espera` (
  `id_cola` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `id_libro` int(11) NOT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` varchar(30) NOT NULL DEFAULT 'en_espera',
  PRIMARY KEY (`id_cola`),
  UNIQUE KEY `uk_cola_usuario_libro_estado` (`id_usuario`,`id_libro`,`estado`),
  KEY `fk_cola_libro` (`id_libro`),
  CONSTRAINT `fk_cola_libro` FOREIGN KEY (`id_libro`) REFERENCES `libro` (`id_libro`),
  CONSTRAINT `fk_cola_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cola_espera`
--

LOCK TABLES `cola_espera` WRITE;
/*!40000 ALTER TABLE `cola_espera` DISABLE KEYS */;
INSERT INTO `cola_espera` VALUES (1,3,1,'2026-06-07 17:05:52','atendido'),(2,5,2,'2026-06-18 18:13:11','atendido'),(3,6,4,'2026-07-09 20:52:01','en_espera');
/*!40000 ALTER TABLE `cola_espera` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobante`
--

DROP TABLE IF EXISTS `comprobante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobante` (
  `id_comprobante` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` varchar(50) NOT NULL,
  PRIMARY KEY (`id_comprobante`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobante`
--

LOCK TABLES `comprobante` WRITE;
/*!40000 ALTER TABLE `comprobante` DISABLE KEYS */;
INSERT INTO `comprobante` VALUES (1,'Boleta'),(2,'Factura');
/*!40000 ALTER TABLE `comprobante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_prestamo`
--

DROP TABLE IF EXISTS `detalle_prestamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_prestamo` (
  `id_detalle_prestamo` int(11) NOT NULL AUTO_INCREMENT,
  `id_prestamo` int(11) NOT NULL,
  `id_libro` int(11) NOT NULL,
  `precio_tiempo` decimal(10,2) NOT NULL DEFAULT 0.00,
  `tiempo` int(11) NOT NULL DEFAULT 1,
  `fecha_compromiso` date DEFAULT NULL,
  `fecha_devolucion` date DEFAULT NULL,
  `estado` enum('prestado','devuelto','perdido','daniado') NOT NULL DEFAULT 'prestado',
  `subtotal` decimal(12,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id_detalle_prestamo`),
  KEY `fk_detalle_prestamo_prestamo` (`id_prestamo`),
  KEY `fk_detalle_prestamo_libro` (`id_libro`),
  CONSTRAINT `fk_detalle_prestamo_libro` FOREIGN KEY (`id_libro`) REFERENCES `libro` (`id_libro`) ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_prestamo_prestamo` FOREIGN KEY (`id_prestamo`) REFERENCES `prestamo` (`id_prestamo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_prestamo`
--

LOCK TABLES `detalle_prestamo` WRITE;
/*!40000 ALTER TABLE `detalle_prestamo` DISABLE KEYS */;
INSERT INTO `detalle_prestamo` VALUES (1,1,1,10.00,3,'2026-06-06','2026-06-04','devuelto',30.00),(2,2,2,10.00,5,'2026-06-08','2026-06-04','devuelto',50.00),(8,8,1,2.50,5,'2026-06-08','2026-06-03','devuelto',12.50),(9,9,1,2.50,5,'2026-06-12','2026-06-07','devuelto',12.50),(10,10,1,2.50,2,'2026-06-09','2026-06-07','devuelto',5.00),(11,11,1,2.50,2,'2026-06-09','2026-06-07','devuelto',5.00),(12,12,2,2.00,4,'2026-06-12','2026-06-08','devuelto',8.00),(13,13,2,2.00,5,'2026-06-23','2026-06-18','devuelto',10.00),(14,14,2,2.00,2,'2026-06-20',NULL,'prestado',4.00),(15,15,4,2.80,2,'2026-06-20',NULL,'prestado',5.60),(16,16,3,3.00,1,'2026-07-10',NULL,'prestado',3.00);
/*!40000 ALTER TABLE `detalle_prestamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `editorial`
--

DROP TABLE IF EXISTS `editorial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `editorial` (
  `id_editorial` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(120) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_editorial`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `editorial`
--

LOCK TABLES `editorial` WRITE;
/*!40000 ALTER TABLE `editorial` DISABLE KEYS */;
INSERT INTO `editorial` VALUES (1,'Alfaguara',1),(2,'Planeta',1),(3,'Penguin Random House',1),(4,'Santillana',1);
/*!40000 ALTER TABLE `editorial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro`
--

DROP TABLE IF EXISTS `libro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro` (
  `id_libro` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(250) NOT NULL,
  `id_editorial` int(11) DEFAULT NULL,
  `id_categoria` int(11) NOT NULL,
  `fecha_public` date DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `isbn` varchar(20) NOT NULL,
  `precio_tiempo` decimal(10,2) NOT NULL DEFAULT 0.00,
  `imagen` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_libro`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `idx_libro_titulo` (`titulo`),
  KEY `idx_libro_categoria_titulo` (`id_categoria`,`titulo`),
  KEY `idx_libro_editorial` (`id_editorial`),
  KEY `idx_libro_estado` (`estado`),
  FULLTEXT KEY `ft_libro_titulo` (`titulo`),
  CONSTRAINT `fk_libro_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON UPDATE CASCADE,
  CONSTRAINT `fk_libro_editorial` FOREIGN KEY (`id_editorial`) REFERENCES `editorial` (`id_editorial`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro`
--

LOCK TABLES `libro` WRITE;
/*!40000 ALTER TABLE `libro` DISABLE KEYS */;
INSERT INTO `libro` VALUES (1,'El Señor de los Anillos',1,1,'1954-07-29',1,'9780007525546',2.50,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(2,'Harry Potter y la Piedra Filosofal',2,1,'1997-06-26',0,'9788478884452',2.00,'https://images.unsplash.com/photo-1519682337058-a94d519337bc'),(3,'Dune',3,2,'1965-08-01',0,'9780441013593',3.00,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(4,'Fundacion',4,2,'1951-06-01',0,'9788497594256',2.80,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(5,'El nombre de la rosa',1,3,'1980-01-01',1,'9788497592580',2.20,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da'),(6,'Asesinato en el Orient Express',2,3,NULL,1,'9780007119318',2.10,'https://images.unsplash.com/photo-1521587760476-6c12a4b040da');
/*!40000 ALTER TABLE `libro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro_autor`
--

DROP TABLE IF EXISTS `libro_autor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro_autor` (
  `id_libro` int(11) NOT NULL,
  `id_autor` int(11) NOT NULL,
  PRIMARY KEY (`id_libro`,`id_autor`),
  KEY `fk_libro_autor_autor` (`id_autor`),
  CONSTRAINT `fk_libro_autor_autor` FOREIGN KEY (`id_autor`) REFERENCES `autor` (`id_autor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_libro_autor_libro` FOREIGN KEY (`id_libro`) REFERENCES `libro` (`id_libro`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro_autor`
--

LOCK TABLES `libro_autor` WRITE;
/*!40000 ALTER TABLE `libro_autor` DISABLE KEYS */;
INSERT INTO `libro_autor` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,2);
/*!40000 ALTER TABLE `libro_autor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` int(11) NOT NULL AUTO_INCREMENT,
  `id_prestamo` int(11) NOT NULL,
  `id_comprobante` int(11) NOT NULL,
  `id_tipo_pago` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `monto` decimal(12,2) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `fk_pago_prestamo` (`id_prestamo`),
  KEY `fk_pago_comprobante` (`id_comprobante`),
  KEY `fk_pago_tipo_pago` (`id_tipo_pago`),
  CONSTRAINT `fk_pago_comprobante` FOREIGN KEY (`id_comprobante`) REFERENCES `comprobante` (`id_comprobante`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pago_prestamo` FOREIGN KEY (`id_prestamo`) REFERENCES `prestamo` (`id_prestamo`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pago_tipo_pago` FOREIGN KEY (`id_tipo_pago`) REFERENCES `tipo_pago` (`id_tipo_pago`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,1,1,1,'2026-06-03 15:48:53',30.00,'Pago total prestamo 1 - efectivo'),(2,2,1,2,'2026-06-03 15:48:53',50.00,'Pago total prestamo 2 - tarjeta');
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prestamo`
--

DROP TABLE IF EXISTS `prestamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prestamo` (
  `id_prestamo` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `id_comprobante` int(11) NOT NULL,
  `fecha_prestamo` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` enum('abierto','cerrado','en_mora','cancelado') NOT NULL DEFAULT 'abierto',
  `serie` varchar(100) NOT NULL,
  `numero` varchar(100) NOT NULL,
  `impuesto` decimal(12,2) NOT NULL DEFAULT 0.00,
  `monto_total` decimal(12,2) NOT NULL DEFAULT 0.00,
  `observaciones` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id_prestamo`),
  KEY `fk_prestamo_usuario` (`id_usuario`),
  KEY `fk_prestamo_comprobante` (`id_comprobante`),
  CONSTRAINT `fk_prestamo_comprobante` FOREIGN KEY (`id_comprobante`) REFERENCES `comprobante` (`id_comprobante`) ON UPDATE CASCADE,
  CONSTRAINT `fk_prestamo_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prestamo`
--

LOCK TABLES `prestamo` WRITE;
/*!40000 ALTER TABLE `prestamo` DISABLE KEYS */;
INSERT INTO `prestamo` VALUES (1,3,1,'2026-06-03 15:48:53','cerrado','B001','000001',0.00,30.00,'Prestamo inicial de prueba'),(2,3,1,'2026-06-03 15:48:53','cerrado','B001','000002',0.00,50.00,'Prestamo inicial de prueba'),(8,4,1,'2026-06-04 01:16:51','cerrado','R-2026','000003',0.00,12.50,''),(9,4,1,'2026-06-07 22:05:27','cerrado','R-2026','000004',0.00,12.50,''),(10,3,1,'2026-06-07 22:15:48','cerrado','R-2026','000005',0.00,5.00,'Reserva realizada por el lector'),(11,3,1,'2026-06-07 22:25:33','cerrado','R-2026','000006',0.00,5.00,'Reserva realizada por el lector'),(12,3,1,'2026-06-08 05:14:18','cerrado','R-2026','000007',0.00,8.00,'Reserva realizada por el lector'),(13,5,1,'2026-06-18 23:10:55','cerrado','R-2026','000008',0.00,10.00,'Reserva realizada por el lector'),(14,5,1,'2026-06-18 23:21:51','abierto','R-2026','000009',0.00,4.00,'Reserva realizada por el lector'),(15,4,1,'2026-06-18 23:26:55','abierto','R-2026','000010',0.00,5.60,'Se le entrega el libro en buenas condiciones'),(16,6,1,'2026-07-10 01:53:32','abierto','R-2026','000011',0.00,3.00,'Reserva realizada por el lector');
/*!40000 ALTER TABLE `prestamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_pago`
--

DROP TABLE IF EXISTS `tipo_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_pago` (
  `id_tipo_pago` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_pago`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_pago`
--

LOCK TABLES `tipo_pago` WRITE;
/*!40000 ALTER TABLE `tipo_pago` DISABLE KEYS */;
INSERT INTO `tipo_pago` VALUES (1,'Efectivo','Pago en caja'),(2,'Tarjeta','Pago con tarjeta de debito o credito'),(3,'Yape','Pago por aplicativo Yape'),(4,'Plin','Pago por aplicativo Plin');
/*!40000 ALTER TABLE `tipo_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_usuario`
--

DROP TABLE IF EXISTS `tipo_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_usuario` (
  `id_tipo_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_usuario`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_usuario`
--

LOCK TABLES `tipo_usuario` WRITE;
/*!40000 ALTER TABLE `tipo_usuario` DISABLE KEYS */;
INSERT INTO `tipo_usuario` VALUES (1,'Administrador','Acceso total al sistema'),(2,'Bibliotecario','Gestiona prestamos y libros'),(3,'Lector','Usuario que puede realizar prestamos');
/*!40000 ALTER TABLE `tipo_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `id_tipo_usuario` int(11) NOT NULL,
  `dni` varchar(20) DEFAULT NULL,
  `usuario` varchar(20) DEFAULT NULL,
  `contrasena` varchar(100) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `correo_e` varchar(160) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `dni` (`dni`),
  UNIQUE KEY `usuario` (`usuario`),
  UNIQUE KEY `correo_e` (`correo_e`),
  KEY `fk_usuario_tipo_usuario` (`id_tipo_usuario`),
  CONSTRAINT `fk_usuario_tipo_usuario` FOREIGN KEY (`id_tipo_usuario`) REFERENCES `tipo_usuario` (`id_tipo_usuario`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,1,'12345678','admin','$2a$10$1ucWvPel9pqiDN1CnVa/LeINvcbpI7HrbjqUPZG0KY/UvBG7w/BX2','Juan','Perez','admin@biblioteca.com','987654321','Av. Central 123',1),(2,2,'87654321','biblio','$2a$10$OttvNJ9i8Hzq3fh1ByX4DOdPcyMBnx0aYGeTC9qrWWSlLrLlqNEaG','Maria','Garcia','maria@biblioteca.com','912345678','Calle 456',1),(3,3,'11223344','lector','$2a$10$hNACD7TNMFWw0gIHtovF/e7Ix.EG1vzs3SPO4M2gt/8Fu0MnyA9Qm','Luis','Torres','luis@correo.com','934567890','Jr. Los Olivos 789',1),(4,3,'60907959','leo','$2a$10$UAnB.7bt.mj9Bvf4jZZtY.6Ei8PnVW6K2JA8Rw3/4hAsijOaNs.xm','Leonardo Stephano','Mejia Rojas','leonardo@biblioteca.com','992889823','Lima',1),(5,3,'60907957','leo240','$2a$10$EA9u6EcmXPGNNLYu4bs0ouCL8vQOx1VFQ9wDSdCLHmI1pipSRqzXe','Leonardo Stephano','Mejia Rojas','leonardo24@biblioteca.com','963258741','Jr. Los Olivos 789',1),(6,3,'41506895','Ana2020','$2a$10$0ZwSp1jbe5dWS9ZJbwIGPOMaL91f74.eR7GnuPy1l6p88HCsFBAQG','Ana ','Garayar','Ana20@biblioteca.com','963258741','Ica',1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'stm_biblioteca'
--

--
-- Dumping routines for database 'stm_biblioteca'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-14 16:22:30
