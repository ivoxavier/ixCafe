-- dbSchemma ixCafe

CREATE TABLE empregados (
    id_empregado INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    pin_acesso VARCHAR(255) NOT NULL, -- Guardar um hash (ex: bcrypt), nunca o PIN em texto plano
    cargo ENUM('Empregado', 'Gerente') NOT NULL DEFAULT 'Empregado',
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

--preco_unitario: Garante que, mesmo que o preço do produto mude no futuro, o registo da venda mantém o valor correto da altura.