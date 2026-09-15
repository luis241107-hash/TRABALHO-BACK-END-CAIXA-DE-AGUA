-- ==========================================
-- BANCO DO SISTEMA CAIXA D'ÁGUA
-- PostgreSQL
-- ==========================================

-- Crie antes o banco "caixaDaAgua" no PostgreSQL e execute este arquivo conectado nele.
-- CREATE DATABASE "caixaDaAgua";

CREATE TABLE IF NOT EXISTS pessoa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL CHECK (length(trim(nome)) >= 3),
    documento VARCHAR(14) NOT NULL UNIQUE,
    idade INTEGER NOT NULL CHECK (idade BETWEEN 0 AND 130),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CLIENTE', 'FUNCIONARIO'))
);

CREATE TABLE IF NOT EXISTS setor (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cliente (
    pessoa_id INTEGER PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    dividas_abertas NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (dividas_abertas >= 0)
);

CREATE TABLE IF NOT EXISTS instalador (
    pessoa_id INTEGER PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    salario NUMERIC(12,2) NOT NULL CHECK (salario > 0),
    turno VARCHAR(20) NOT NULL CHECK (turno IN ('MANHA', 'TARDE', 'NOITE')),
    habilidade VARCHAR(30) NOT NULL CHECK (
        habilidade IN ('INSTALACAO', 'ADMINISTRACAO', 'FINANCEIRO', 'LOGISTICA')
    ),
    setor_id INTEGER NOT NULL REFERENCES setor(id)
);

CREATE TABLE IF NOT EXISTS caixa_da_agua (
    id SERIAL PRIMARY KEY,
    marca VARCHAR(60) NOT NULL CHECK (length(trim(marca)) >= 2),
    modelo VARCHAR(80) NOT NULL CHECK (length(trim(modelo)) >= 2),
    dimensao DOUBLE PRECISION[] NOT NULL CHECK (array_length(dimensao, 1) = 3),
    cor VARCHAR(30) NOT NULL CHECK (
        cor IN ('AZUL_ESCURO', 'PRETO', 'AZUL_FRACO', 'CIANO')
    ),
    material VARCHAR(30) NOT NULL CHECK (
        material IN ('POLIETILENO', 'FIBRA_DE_VIDRO', 'INOX')
    ),
    formato VARCHAR(20) NOT NULL CHECK (
        formato IN ('PEQUENA', 'MEDIA', 'GRANDE')
    ),
    preco NUMERIC(12,2) NOT NULL CHECK (preco > 0),
    estoque INTEGER NOT NULL DEFAULT 0 CHECK (estoque >= 0)
);

CREATE TABLE IF NOT EXISTS venda (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL REFERENCES cliente(pessoa_id),
    produto_id INTEGER NOT NULL REFERENCES caixa_da_agua(id),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    valor_total NUMERIC(12,2) NOT NULL CHECK (valor_total > 0),
    data_venda TIMESTAMP NOT NULL,
    responsavel_id INTEGER NOT NULL REFERENCES instalador(pessoa_id),
    forma_pagamento VARCHAR(20) NOT NULL CHECK (
        forma_pagamento IN ('DINHEIRO', 'PIX', 'CARTAO', 'BOLETO')
    )
);

CREATE TABLE IF NOT EXISTS movimentacao (
    id SERIAL PRIMARY KEY,
    valor NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    pagador VARCHAR(150) NOT NULL,
    recebedor VARCHAR(150) NOT NULL,
    responsavel_id INTEGER REFERENCES instalador(pessoa_id),
    descricao VARCHAR(255) NOT NULL CHECK (length(trim(descricao)) >= 3),
    venda_id INTEGER UNIQUE REFERENCES venda(id)
);

CREATE TABLE IF NOT EXISTS caixa (
    id INTEGER PRIMARY KEY CHECK (id = 1),
    saldo NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (saldo >= 0)
);

-- ==========================================
-- DADOS INICIAIS
-- ==========================================

INSERT INTO setor (nome)
VALUES ('ADMINISTRATIVO'), ('FINANCEIRO'), ('LOGISTICA')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO caixa (id, saldo)
VALUES (1, 0)
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- CONSULTAS ÚTEIS
-- ==========================================

-- Saldo:
-- SELECT saldo FROM caixa WHERE id = 1;

-- Produtos e estoque:
-- SELECT * FROM caixa_da_agua ORDER BY id;

-- Funcionários e setores:
-- SELECT p.id, p.nome, i.habilidade, s.nome AS setor
-- FROM pessoa p
-- JOIN instalador i ON i.pessoa_id = p.id
-- JOIN setor s ON s.id = i.setor_id;

-- Histórico financeiro:
-- SELECT * FROM movimentacao ORDER BY data_hora DESC;

-- Vendas:
-- SELECT * FROM venda ORDER BY data_venda DESC;
