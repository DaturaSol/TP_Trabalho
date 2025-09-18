-- Este arquivo define a estrutura completa do banco de dados.
-- Todos os membros da equipe devem adicionar suas tabelas aqui.

-- Módulo: Administração (Aluno 1)
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('ADMIN', 'GESTOR', 'RECRUTADOR', 'FINANCEIRO'))
);

-- Módulo: Candidatura (Aluno 2)
CREATE TABLE IF NOT EXISTS candidatos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_completo TEXT NOT NULL,
    cpf TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    telefone TEXT,
    formacao TEXT,
    experiencia TEXT
);

CREATE TABLE IF NOT EXISTS candidaturas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    candidato_id INTEGER NOT NULL,
    vaga_id INTEGER NOT NULL,
    data_candidatura TEXT NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('PENDENTE', 'EM_ANALISE', 'APROVADO', 'REPROVADO')),
    FOREIGN KEY (candidato_id) REFERENCES candidatos(id),
    FOREIGN KEY (vaga_id) REFERENCES vagas(id)
);

-- Adicione aqui as tabelas para Recrutamento, Financeiro e Prestação de Serviço...
-- Exemplo para Recrutamento (Aluno 3)
CREATE TABLE IF NOT EXISTS vagas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    descricao TEXT,
    salario REAL,
    status TEXT NOT NULL CHECK(status IN ('ABERTA', 'FECHADA'))
);

-- Adicione um usuário admin padrão para testes iniciais
INSERT INTO usuarios (username, password_hash, role) VALUES ('admin', 'admin123', 'ADMIN');