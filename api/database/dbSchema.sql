-- MySQL dump 10.13  Distrib 8.0.44, for macos15 (x86_64)
--
-- Host: localhost    Database: ixCafe
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `bearer_tokens`
--

DROP TABLE IF EXISTS `bearer_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bearer_tokens` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `token_value` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `token_value` (`token_value`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bearer_tokens`
--

LOCK TABLES `bearer_tokens` WRITE;
/*!40000 ALTER TABLE `bearer_tokens` DISABLE KEYS */;
INSERT INTO `bearer_tokens` VALUES (1,'9f38c7a1b24e4d89a67e5f1b3c2d90ef','Token de Administrador',1,'2025-10-23 19:45:09'),(2,'8bda913c4f2e5a77b6e3d8c1f09a45dc','Token de Empregado Comum',1,'2025-10-23 19:45:09'),(3,'3c29f6a84e1b7d5a9b2c4f8e0d6a3b71','Token de Gerente',1,'2025-10-23 19:45:09');
/*!40000 ALTER TABLE `bearer_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias_produto`
--

DROP TABLE IF EXISTS `categorias_produto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_produto` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias_produto`
--

LOCK TABLES `categorias_produto` WRITE;
/*!40000 ALTER TABLE `categorias_produto` DISABLE KEYS */;
INSERT INTO `categorias_produto` VALUES (1,'Bolos');
/*!40000 ALTER TABLE `categorias_produto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contas`
--

DROP TABLE IF EXISTS `contas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contas` (
  `id_conta` int NOT NULL AUTO_INCREMENT,
  `id_mesa` int NOT NULL,
  `data_abertura` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_fecho` timestamp NULL DEFAULT NULL,
  `estado` enum('Aberta','Paga','Cancelada') NOT NULL DEFAULT 'Aberta',
  `total_pago` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id_conta`),
  KEY `id_mesa` (`id_mesa`),
  CONSTRAINT `contas_ibfk_1` FOREIGN KEY (`id_mesa`) REFERENCES `mesas` (`id_mesa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contas`
--

LOCK TABLES `contas` WRITE;
/*!40000 ALTER TABLE `contas` DISABLE KEYS */;
/*!40000 ALTER TABLE `contas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empregados`
--

DROP TABLE IF EXISTS `empregados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empregados` (
  `id_empregado` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `pin_acesso` varchar(255) NOT NULL,
  `cargo` enum('Empregado','Gerente') NOT NULL DEFAULT 'Empregado',
  `token_role` int NOT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  `data_criacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_empregado`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empregados`
--

LOCK TABLES `empregados` WRITE;
/*!40000 ALTER TABLE `empregados` DISABLE KEYS */;
INSERT INTO `empregados` VALUES (1,'Ivo','$2a$11$qGNilbO2FIbjBE.ja6La.e7fD4YpzrvWFlw9I0k2CZO2w9i3fYkZW','Gerente',3,1,'2025-10-23 23:00:00'),(2,'Admin','$2a$11$nA1MPJZAPAXNOtIy/OFqxeHBx3nG2Ftb5swT66UI0QAuEuACbpzNO','Gerente',3,1,'2025-11-03 00:00:00');
/*!40000 ALTER TABLE `empregados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linhas_pedido`
--

DROP TABLE IF EXISTS `linhas_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linhas_pedido` (
  `id_linha` int NOT NULL AUTO_INCREMENT,
  `id_pedido` int NOT NULL,
  `id_produto` int NOT NULL,
  `quantidade` int NOT NULL DEFAULT '1',
  `preco_unitario` decimal(10,2) NOT NULL,
  `observacoes` text,
  PRIMARY KEY (`id_linha`),
  KEY `id_pedido` (`id_pedido`),
  KEY `id_produto` (`id_produto`),
  CONSTRAINT `linhas_pedido_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE,
  CONSTRAINT `linhas_pedido_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linhas_pedido`
--

LOCK TABLES `linhas_pedido` WRITE;
/*!40000 ALTER TABLE `linhas_pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `linhas_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mesas`
--

DROP TABLE IF EXISTS `mesas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesas` (
  `id_mesa` int NOT NULL AUTO_INCREMENT,
  `numero_mesa` varchar(10) NOT NULL,
  `localizacao` varchar(50) DEFAULT NULL,
  `capacidade` int NOT NULL DEFAULT '4',
  PRIMARY KEY (`id_mesa`),
  UNIQUE KEY `numero_mesa` (`numero_mesa`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesas`
--

LOCK TABLES `mesas` WRITE;
/*!40000 ALTER TABLE `mesas` DISABLE KEYS */;
INSERT INTO `mesas` VALUES (1,'1','Esplanada',3);
/*!40000 ALTER TABLE `mesas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id_pedido` int NOT NULL AUTO_INCREMENT,
  `id_conta` int NOT NULL,
  `id_empregado` int NOT NULL,
  `data_hora` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_pedido` enum('Recebido','Em preparação','Entregue','Cancelado') NOT NULL DEFAULT 'Recebido',
  `observacoes` text,
  PRIMARY KEY (`id_pedido`),
  KEY `id_conta` (`id_conta`),
  KEY `id_empregado` (`id_empregado`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_conta`) REFERENCES `contas` (`id_conta`),
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`id_empregado`) REFERENCES `empregados` (`id_empregado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produtos` (
  `id_produto` int NOT NULL AUTO_INCREMENT,
  `id_categoria` int NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `preco` decimal(10,2) NOT NULL,
  `disponivel` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_produto`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `produtos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias_produto` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Admin'),(2,'Empregado'),(3,'Gerente');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_roles`
--

DROP TABLE IF EXISTS `token_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_roles` (
  `token_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`token_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `token_roles_ibfk_1` FOREIGN KEY (`token_id`) REFERENCES `bearer_tokens` (`token_id`) ON DELETE CASCADE,
  CONSTRAINT `token_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_roles`
--

LOCK TABLES `token_roles` WRITE;
/*!40000 ALTER TABLE `token_roles` DISABLE KEYS */;
INSERT INTO `token_roles` VALUES (1,1),(1,2),(2,2),(1,3),(3,3);
/*!40000 ALTER TABLE `token_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ixCafe'
--

--
-- Dumping routines for database 'ixCafe'
--
/*!50003 DROP PROCEDURE IF EXISTS `sp_CriarEmpregado` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CriarEmpregado`(
    IN p_nome VARCHAR(100),
    IN p_pin_acesso VARCHAR(255), 
    IN p_cargo ENUM('Empregado', 'Gerente'),
    IN p_token_role INT,
    OUT perrorCode INT,
    OUT perrorMessage VARCHAR(255)
)
main:BEGIN
    
    
    
    DECLARE EmpregadoJaExiste INT;
    
    SELECT COUNT(*)
    INTO EmpregadoJaExiste
    FROM empregados
    WHERE nome = p_nome
    AND token_role = p_token_role
    AND ativo = 1
    LIMIT 1;
    
    IF EmpregadoJaExiste > 0 THEN
		SET perrorCode = 99;
        SET perrorMessage = CASE
							WHEN p_token_role = 2 THEN 'Empregado Já Existe.'
                            WHEN p_token_role = 3 THEN 'Gerente Já Existe.'
							ELSE 'Erro.'
                            END;
        LEAVE main;
	ELSE
    
    
    INSERT INTO `empregados` (
        `nome`,
        `pin_acesso`,
        `cargo`,
        `token_role`,
        `ativo`,        
        `data_criacao` 
    )
    VALUES (
        p_nome,
        p_pin_acesso,
        p_cargo,
        p_token_role,
        1,
        CURDATE()
        
    );
    END IF;
    
    
    SET perrorCode = 0;
    
    
END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_CriarMesa` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CriarMesa`(
IN p_numero_mesa INT,
IN p_localizacao VARCHAR(255),
IN p_capacidade INT,
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255))
main: BEGIN


DECLARE mesaJaExiste INT;

SELECT COUNT(*)
INTO mesaJaExiste
FROM mesas
WHERE numero_mesa = p_numero_mesa
LIMIT 1;

IF mesaJaExiste > 0 THEN
SET perrorCode = 99;
SET perrorMessage = 'Mesa já existe.';
ELSE
INSERT INTO mesas (numero_mesa, localizacao,capacidade)
			VALUES(p_numero_mesa,p_localizacao,p_capacidade);
END IF;

SET perrorCode = 0;

END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_criarProduto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_criarProduto`(IN p_categoria INT,
IN  p_nome VARCHAR(255),
IN  p_descricao VARCHAR(255),
IN  p_preco DECIMAL (8,2),
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255))
main: BEGIN

DECLARE ProdutoJaExiste INT;

SELECT COUNT(*)
INTO ProdutoJaExiste
FROM produtos
WHERE id_categoria = p_categoria
AND nome = p_nome
LIMIT 1;

IF ProdutoJaExiste > 0 THEN 
	SET perrorCode = 99;
    SET perrorMessage = 'Error: Já existe.';
    LEAVE main;
ELSE
 INSERT INTO produtos (id_categoria, nome, descricao, preco)
 VALUES(p_categoria,p_nome,p_descricao, p_preco, 1);
 END IF;
 
 SET perrorCode = 0;



END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_DesativarEmpregado` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DesativarEmpregado`(
IN p_nome VARCHAR(255),
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255))
main:BEGIN

DECLARE EmpregadoAtivo INT;


SELECT COUNT(*)
INTO EmpregadoAtivo
FROM empregados
WHERE nome = p_nome
AND ativo = 1;

IF EmpregadoAtivo > 0 THEN
START TRANSACTION;

UPDATE empregados
SET ativo = 0
WHERE nome = p_nome;

COMMIT;

ELSE
SET perrorCode = 99;
SET perrorMessage = 'Empregado não existe OU já está desativo';
LEAVE main;
END IF;


SET perrorCode = 0;

END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_disponibilidade` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_disponibilidade`(
								  IN p_nome VARCHAR(255),
								  IN p_disponivel BIT,
								  OUT perrorCode INT,
                                  OUT perrorMessage VARCHAR(255))
main:BEGIN




UPDATE produtos
SET disponivel = p_disponivel
WHERE nome = EXISTS((SELECT nome
		   FROM produtos
           WHERE nome = p_nome));

SET perrorCode = 0;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_EditarMesa` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_EditarMesa`(IN p_numero_mesa INT,
IN  p_localizacao VARCHAR(255),
IN p_capacidade INT,
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255))
main: BEGIN

DECLARE MesaExiste INT;

SELECT COUNT(*)
INTO MesaExiste
FROM mesas
WHERE numero_mesa = p_numero_mesa;

IF MesaExiste > 0 THEN
START TRANSACTION;

UPDATE mesas
SET localizacao = p_localizacao,
	capacidade = p_capacidade
WHERE numero_mesa = p_numero_mesa;
ELSE
SET perrorCode = 99;
SET perrorMessage = "Mesa não existe.";
END IF;


END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_EliminarEmpregado` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_EliminarEmpregado`(
IN p_nome VARCHAR(255),
IN p_token_role INT,
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255)
)
main: BEGIN

	DECLARE EmpregadoExiste INT;
    
    SELECT COUNT(*)
    INTO EmpregadoExiste
    FROM empregados
    WHERE nome = p_nome
    AND token_role = p_token_role
    LIMIT 1;


	IF EmpregadoExiste > 0 THEN
	   DELETE FROM empregados
       WHERE nome = p_nome
       AND token_role = p_token_role;
	ELSE 
    SET perrorCode = 99;
    SET perrorMessage = 'Erro: Nome não existe!';
	LEAVE main;
	END IF;
    
    SET perrorCode = 0;
    
END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_EliminarMesa` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_EliminarMesa`(
IN p_numero_mesa INT,
OUT perrorCode INT,
OUT perrorMessage VARCHAR(255)
)
main: BEGIN

	DECLARE MesaExiste INT;
    
    SELECT COUNT(*)
    INTO MesaExiste
    FROM mesas
    WHERE numero_mesa = p_numero_mesa
    LIMIT 1;


	IF MesaExiste > 0 THEN
	   DELETE FROM mesas
       WHERE numero_mesa = p_numero_mesa;
	ELSE 
    SET perrorCode = 99;
    SET perrorMessage = 'Erro: Mesa não existe!';
	LEAVE main;
	END IF;
    
    SET perrorCode = 0;
    
END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ListarEmpregado` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ListarEmpregado`( )
main:BEGIN

SELECT nome, cargo
FROM empregados;


END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ListarMesas` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ListarMesas`()
main: BEGIN


SELECT numero_mesa, localizacao, capacidade
FROM mesas;


END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_login` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_login`(
    IN p_nome VARCHAR(255),
    IN p_token_role INT,
    OUT pidEmpregado BIGINT,
    OUT pPinAcesso VARCHAR(255),
    OUT perrorCode INT,
    OUT perrorMessage VARCHAR(255)
)
main:BEGIN
    
    
    SET perrorCode = 0;
    SET perrorMessage = '';
    SET pidEmpregado = 0;
    SET pPinAcesso = '';

    
    SELECT 
        id_empregado, pin_acesso
    INTO 
        pidEmpregado, pPinAcesso
    FROM empregados
    WHERE nome = p_nome
      AND token_role = p_token_role
    LIMIT 1;

    
    IF pidEmpregado = 0 THEN 
        SET perrorCode = 99;
        SET perrorMessage = 'Erro: Utilizador ou role inválidos';
    END IF;

END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_NovaCategoria` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_NovaCategoria`(IN p_categoria VARCHAR(255),
 OUT perrorCode INT,
 OUT perrorMessage VARCHAR(255))
main:BEGIN


DECLARE CategoriaJaExiste INT;

SELECT COUNT(*)
INTO CategoriaJaExiste
FROM categorias_produto
WHERE nome = TRIM(p_categoria);

IF CategoriaJaExiste = 0 THEN
INSERT INTO categorias_produto (nome) VALUES (TRIM(p_categoria));
ELSE
SET perrorCode = 99;
SET perrorMessage = 'Error: Categoria ja existe';
LEAVE main;
END IF;

SET perrorCode = 0;

END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_novoPedido` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_novoPedido`(
    IN p_pedido JSON,
    OUT perrorCode INT,
    OUT perrorMessage VARCHAR(255)
)
main: BEGIN

    -- ... (Declarações de variáveis e Handler de Erro - tudo igual) ...
    DECLARE EmpregadoExiste INT;
    DECLARE MesaExiste INT;
    DECLARE ProdutosValidosCount INT;
    DECLARE v_idEmpregado INT;
    DECLARE v_idMesa INT;
    DECLARE v_idConta INT;
    DECLARE v_idPedidoNovo BIGINT;
    DECLARE v_itemsCount INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK; 
        SET perrorCode = 500;
        SET perrorMessage = 'Erro interno no servidor. A transação foi revertida.';
    END;

    -- --- 1. DEFINIR VALORES PADRÃO E EXTRAIR DADOS ---
    -- ... (Tudo igual) ...
    SET perrorCode = 1;
    SET perrorMessage = 'Erro desconhecido';
    SET v_idEmpregado = JSON_UNQUOTE(JSON_EXTRACT(p_pedido, '$.idEmpregado'));
    SET v_idMesa = JSON_UNQUOTE(JSON_EXTRACT(p_pedido, '$.idMesa'));
    SET v_itemsCount = JSON_LENGTH(p_pedido, '$.pedidos');

    -- --- 2. VALIDAÇÕES ---
    -- ... (Tudo igual) ...
    IF v_itemsCount = 0 THEN
        SET perrorCode = 104; SET perrorMessage = 'O pedido não contém itens.'; LEAVE main;
    END IF;
    SELECT COUNT(*) INTO EmpregadoExiste FROM empregados WHERE id = v_idEmpregado;
    IF EmpregadoExiste = 0 THEN
        SET perrorCode = 101; SET perrorMessage = 'Empregado não existe.'; LEAVE main; 
    END IF;
    SELECT COUNT(*) INTO MesaExiste FROM mesas WHERE id = v_idMesa;
    IF MesaExiste = 0 THEN
        SET perrorCode = 102; SET perrorMessage = 'Mesa não existe.'; LEAVE main;
    END IF;
    SELECT COUNT(p.id) INTO ProdutosValidosCount
    FROM produtos p
    JOIN JSON_TABLE(
        p_pedido, '$.pedidos[*]'
        COLUMNS ( idProduto INT PATH '$.idProduto' )
    ) AS ItensPedido ON p.id = ItensPedido.idProduto;
    IF ProdutosValidosCount <> v_itemsCount THEN
        SET perrorCode = 103;
        SET perrorMessage = 'Um ou mais produtos no pedido não existem.'; LEAVE main;
    END IF;

    -- --- 3. LÓGICA PRINCIPAL (Transação) ---
    
    START TRANSACTION;

    -- A. Encontrar ou Criar a Conta
    -- ... (Tudo igual) ...
    SET v_idConta = NULL;
    SELECT id_conta INTO v_idConta 
    FROM contas 
    WHERE id_mesa = v_idMesa AND estado = 'Aberta' 
    LIMIT 1;
    IF v_idConta IS NULL THEN 
        INSERT INTO contas (id_mesa, estado) VALUES (v_idMesa, 'Aberta');
        SET v_idConta = LAST_INSERT_ID();
    END IF;
    
    -- B. Inserir o "Cabeçalho" do Pedido
    -- ... (Tudo igual) ...
    INSERT INTO pedidos (id_conta, id_empregado)
    VALUES (v_idConta, v_idEmpregado);
    SET v_idPedidoNovo = LAST_INSERT_ID();
    
    -- C. Inserir os ITENS do Pedido (na sua tabela 'linhas_pedido')
    --    *** ESTA SECÇÃO FOI ATUALIZADA ***
    
    INSERT INTO linhas_pedido (
        id_pedido,      -- O ID do passo B
        id_produto,     -- Do JSON
        quantidade,     -- Do JSON
        preco_unitario, -- Da tabela 'produtos'
        observacoes     -- Do JSON
    )
    SELECT 
        v_idPedidoNovo,       -- ID do Pedido (cabeçalho)
        jt.idProduto,         -- ID do produto do JSON
        jt.quantidade,        -- Quantidade do JSON
        p.preco,              -- Preço atual (da tabela 'produtos')
        jt.observacoes        -- Observações do JSON
    FROM 
        JSON_TABLE(
            p_pedido,
            "$.pedidos[*]"
            COLUMNS (
                idProduto INT PATH "$.idProduto",
                quantidade INT PATH "$.quantidade",
                observacoes TEXT PATH "$.observacoes"
            )
        ) AS jt
    -- Junta com a tabela de produtos para buscar o preço
    JOIN 
        produtos p ON p.id = jt.idProduto;
        
    COMMIT;

    -- --- 4. SUCESSO ---
    SET perrorCode = 0;
    SET perrorMessage = 'Pedido criado com sucesso.';

END main ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_ValidateTokenAndGetRoles` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ValidateTokenAndGetRoles`(
    IN p_token_value VARCHAR(255)
)
BEGIN
   
    SELECT 
        r.role_name
    FROM 
        `bearer_tokens` AS t
    INNER JOIN 
        `token_roles` AS tr ON t.token_id = tr.token_id
    INNER JOIN 
        `roles` AS r ON tr.role_id = r.role_id
    WHERE 
        t.token_value = p_token_value
        AND t.is_active = TRUE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-19 23:23:11
