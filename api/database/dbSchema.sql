-- dbSchemma ixCafe

DROP SCHEMA IF EXISTS ixCafe;


CREATE SCHEMA IF NOT EXISTS ixCafe;

USE ixCafe;

CREATE TABLE empregados (
    id_empregado INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    pin_acesso VARCHAR(255) NOT NULL, -- Guardar um hash (ex: bcrypt), nunca o PIN em texto plano
    cargo ENUM('Empregado', 'Gerente') NOT NULL DEFAULT 'Empregado',
    token_role INT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- pin_acesso: Essencial para o login no PDA. Deve ser armazenado de forma segura (hashed).

-- ativo: Permite desativar um funcionário sem apagar o seu histórico de pedidos.


CREATE TABLE mesas (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    numero_mesa VARCHAR(10) NOT NULL UNIQUE, -- Ex: "5", "T1" (para esplanada), etc.
    localizacao VARCHAR(50), -- Ex: "Interior", "Esplanada"
    capacidade INT NOT NULL DEFAULT 4
) ENGINE=InnoDB;

CREATE TABLE categorias_produto (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE produtos (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_categoria) REFERENCES categorias_produto(id_categoria)
) ENGINE=InnoDB;




-- preco: O tipo de dados DECIMAL é ideal para valores monetários, evitando problemas de arredondamento.

-- disponivel: Permite retirar um produto do menu sem o apagar da base de dados.




CREATE TABLE contas (
    id_conta INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    data_abertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fecho TIMESTAMP NULL,
    estado ENUM('Aberta', 'Paga', 'Cancelada') NOT NULL DEFAULT 'Aberta',
    total_pago DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (id_mesa) REFERENCES mesas(id_mesa)
) ENGINE=InnoDB;


CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_conta INT NOT NULL,
    id_empregado INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_pedido ENUM('Recebido', 'Em preparação', 'Entregue', 'Cancelado') NOT NULL DEFAULT 'Recebido',
    observacoes TEXT, -- Observações gerais para o pedido todo
    FOREIGN KEY (id_conta) REFERENCES contas(id_conta),
    FOREIGN KEY (id_empregado) REFERENCES empregados(id_empregado)
) ENGINE=InnoDB;


CREATE TABLE linhas_pedido (
    id_linha INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_produto INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    preco_unitario DECIMAL(10, 2) NOT NULL, -- Preço no momento do pedido
    observacoes TEXT, -- Ex: "Café sem açúcar", "Bolo com canela extra"
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE, -- Se o pedido for apagado, as linhas vão junto
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
) ENGINE=InnoDB;

-- preco_unitario: Garante que, mesmo que o preço do produto mude no futuro, o registo da venda mantém o valor correto da altura.


-- 1. Tabela para as definições das roles
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` INT AUTO_INCREMENT PRIMARY KEY,
  `role_name` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Tabela para os tokens
CREATE TABLE IF NOT EXISTS `bearer_tokens` (
  `token_id` INT AUTO_INCREMENT PRIMARY KEY,
  `token_value` VARCHAR(255) NOT NULL UNIQUE,
  `description` VARCHAR(255) NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Tabela de Junção (Muitos-para-Muitos)
CREATE TABLE IF NOT EXISTS `token_roles` (
  `token_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`token_id`, `role_id`),
  FOREIGN KEY (`token_id`) REFERENCES `bearer_tokens`(`token_id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --- DADOS DE EXEMPLO (Opcional) ---

-- Inserir as roles que a sua API vai usar
INSERT INTO `roles` (role_name) VALUES ('Admin'), ('Empregado'), ('Gerente');

-- Inserir os tokens fixos
INSERT INTO `bearer_tokens` (token_value, description) VALUES 
('9f38c7a1b24e4d89a67e5f1b3c2d90ef', 'Token de Administrador'),
('8bda913c4f2e5a77b6e3d8c1f09a45dc', 'Token de Empregado Comum'),
('3c29f6a84e1b7d5a9b2c4f8e0d6a3b71', 'Token de Gerente');

-- Associar os tokens às roles
-- (Assumindo que os IDs são 1='Admin', 2='Empregado', 3='Gerente')

-- Token 1 (Admin) tem as roles 'Admin' e 'Empregado' e 'Gerente'
INSERT INTO `token_roles` (token_id, role_id) VALUES (1, 1), (1, 2), (1,3);

-- Token 2 (Empregado) tem a role 'Empregado'
INSERT INTO `token_roles` (token_id, role_id) VALUES (2, 2);

-- Token 3 (Gerente) tem a role 'Gerente'
INSERT INTO `token_roles` (token_id, role_id) VALUES (3, 3);

/*Stored Procedures*/

DROP PROCEDURE IF EXISTS sp_ValidateTokenAndGetRoles;

DELIMITER $$

CREATE PROCEDURE `sp_ValidateTokenAndGetRoles`(
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
END$$

DROP PROCEDURE IF EXISTS sp_CriarEmpregado;

DELIMITER $$

CREATE PROCEDURE `sp_CriarEmpregado`(
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
    WHERE nome = p_nome;
    
    IF EmpregadoJaExiste > 0 THEN
		SET perrorCode = 99;
        SET perrorMessage = 'Empregado Já Existe.';
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
        p_token_role
    );
    END IF;
    
    
    SET perrorCode = 0;
    
    
END main$$

DELIMITER ;
;