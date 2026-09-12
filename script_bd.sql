-- script_bd.sql — DDL completo do Clyvo Vet (gerado a partir das migrations V1 e V3)

-- =====================================================================
-- V1 - Criação do schema Clyvo Vet
-- =====================================================================

CREATE TABLE tb_tutor (
    id            BIGSERIAL PRIMARY KEY,
    firebase_uid  VARCHAR(128) NOT NULL UNIQUE,
    nome          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    senha_hash    VARCHAR(255),
    telefone      VARCHAR(20),
    perfil        VARCHAR(20) NOT NULL DEFAULT 'TUTOR' CHECK (perfil IN ('TUTOR', 'VETERINARIO')),
    criado_em     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  tb_tutor IS 'Tutores e veterinários que acessam o sistema';
COMMENT ON COLUMN tb_tutor.id IS 'Identificador interno do tutor';
COMMENT ON COLUMN tb_tutor.firebase_uid IS 'UID do Firebase Authentication, chave de ligação com o app mobile';
COMMENT ON COLUMN tb_tutor.nome IS 'Nome completo do tutor ou veterinário';
COMMENT ON COLUMN tb_tutor.email IS 'E-mail único, usado no login web';
COMMENT ON COLUMN tb_tutor.senha_hash IS 'Hash BCrypt da senha, usado apenas no login web (Thymeleaf)';
COMMENT ON COLUMN tb_tutor.telefone IS 'Telefone de contato';
COMMENT ON COLUMN tb_tutor.perfil IS 'Perfil de acesso: TUTOR ou VETERINARIO';
COMMENT ON COLUMN tb_tutor.criado_em IS 'Data e hora de criação do registro';

CREATE TABLE tb_pet (
    id               BIGSERIAL PRIMARY KEY,
    tutor_id         BIGINT NOT NULL REFERENCES tb_tutor (id) ON DELETE CASCADE,
    nome             VARCHAR(100) NOT NULL,
    especie          VARCHAR(20) NOT NULL CHECK (especie IN ('cachorro', 'gato', 'ave', 'roedor', 'outro')),
    raca             VARCHAR(100),
    sexo             VARCHAR(10) NOT NULL CHECK (sexo IN ('Macho', 'Fêmea')),
    data_nascimento  DATE,
    peso             NUMERIC(5,2) CHECK (peso IS NULL OR (peso >= 0.1 AND peso <= 120)),
    castrado         BOOLEAN NOT NULL DEFAULT FALSE,
    foto             VARCHAR(500),
    observacoes      VARCHAR(1000),
    criado_em        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  tb_pet IS 'Pets cadastrados pelos tutores';
COMMENT ON COLUMN tb_pet.id IS 'Identificador interno do pet';
COMMENT ON COLUMN tb_pet.tutor_id IS 'Tutor dono do pet (FK tb_tutor)';
COMMENT ON COLUMN tb_pet.nome IS 'Nome do pet';
COMMENT ON COLUMN tb_pet.especie IS 'Espécie: cachorro, gato, ave, roedor ou outro';
COMMENT ON COLUMN tb_pet.raca IS 'Raça do pet';
COMMENT ON COLUMN tb_pet.sexo IS 'Sexo do pet: Macho ou Fêmea';
COMMENT ON COLUMN tb_pet.data_nascimento IS 'Data de nascimento no formato yyyy-MM-dd';
COMMENT ON COLUMN tb_pet.peso IS 'Peso em quilogramas';
COMMENT ON COLUMN tb_pet.castrado IS 'Indica se o pet é castrado';
COMMENT ON COLUMN tb_pet.foto IS 'Caminho ou URI local da foto do pet, enviado pelo app';
COMMENT ON COLUMN tb_pet.observacoes IS 'Observações livres sobre o pet, ex.: alergias';
COMMENT ON COLUMN tb_pet.criado_em IS 'Data e hora de criação do registro';

CREATE TABLE tb_consulta (
    id           BIGSERIAL PRIMARY KEY,
    pet_id       BIGINT NOT NULL REFERENCES tb_pet (id) ON DELETE CASCADE,
    tutor_id     BIGINT NOT NULL REFERENCES tb_tutor (id) ON DELETE CASCADE,
    data         DATE NOT NULL,
    horario      VARCHAR(5) NOT NULL,
    clinica      VARCHAR(150) NOT NULL,
    veterinario  VARCHAR(150),
    motivo       VARCHAR(255) NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'agendada' CHECK (status IN ('agendada', 'concluida', 'cancelada')),
    observacoes  VARCHAR(1000),
    criado_em    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  tb_consulta IS 'Consultas veterinárias agendadas, concluídas ou canceladas';
COMMENT ON COLUMN tb_consulta.id IS 'Identificador interno da consulta';
COMMENT ON COLUMN tb_consulta.pet_id IS 'Pet consultado (FK tb_pet)';
COMMENT ON COLUMN tb_consulta.tutor_id IS 'Tutor dono do pet no momento da consulta (FK tb_tutor)';
COMMENT ON COLUMN tb_consulta.data IS 'Data da consulta no formato yyyy-MM-dd';
COMMENT ON COLUMN tb_consulta.horario IS 'Horário da consulta no formato HH:mm (24h)';
COMMENT ON COLUMN tb_consulta.clinica IS 'Nome da clínica/unidade';
COMMENT ON COLUMN tb_consulta.veterinario IS 'Nome do veterinário responsável';
COMMENT ON COLUMN tb_consulta.motivo IS 'Motivo da consulta';
COMMENT ON COLUMN tb_consulta.status IS 'Status: agendada, concluida ou cancelada';
COMMENT ON COLUMN tb_consulta.observacoes IS 'Observações livres sobre a consulta';
COMMENT ON COLUMN tb_consulta.criado_em IS 'Data e hora de criação do registro';

CREATE TABLE tb_prontuario (
    id            BIGSERIAL PRIMARY KEY,
    consulta_id   BIGINT NOT NULL UNIQUE REFERENCES tb_consulta (id) ON DELETE CASCADE,
    diagnostico   VARCHAR(2000) NOT NULL,
    prescricao    VARCHAR(2000),
    peso_aferido  NUMERIC(5,2) NOT NULL CHECK (peso_aferido >= 0.1 AND peso_aferido <= 120),
    retorno_em    DATE,
    registrado_por VARCHAR(150) NOT NULL,
    criado_em     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  tb_prontuario IS 'Prontuário de atendimento, um por consulta (1:1)';
COMMENT ON COLUMN tb_prontuario.id IS 'Identificador interno do prontuário';
COMMENT ON COLUMN tb_prontuario.consulta_id IS 'Consulta atendida (FK 1:1 tb_consulta)';
COMMENT ON COLUMN tb_prontuario.diagnostico IS 'Diagnóstico registrado pelo veterinário';
COMMENT ON COLUMN tb_prontuario.prescricao IS 'Prescrição/tratamento indicado';
COMMENT ON COLUMN tb_prontuario.peso_aferido IS 'Peso do pet aferido durante o atendimento, em kg';
COMMENT ON COLUMN tb_prontuario.retorno_em IS 'Data sugerida para retorno, se houver';
COMMENT ON COLUMN tb_prontuario.registrado_por IS 'Nome do veterinário que registrou o atendimento';
COMMENT ON COLUMN tb_prontuario.criado_em IS 'Data e hora de criação do registro';

CREATE TABLE tb_vacina (
    id             BIGSERIAL PRIMARY KEY,
    pet_id         BIGINT NOT NULL REFERENCES tb_pet (id) ON DELETE CASCADE,
    nome           VARCHAR(150) NOT NULL,
    data_aplicacao DATE NOT NULL,
    proxima_dose   DATE,
    lote           VARCHAR(50),
    veterinario    VARCHAR(150),
    criado_em      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  tb_vacina IS 'Doses de vacina aplicadas em cada pet';
COMMENT ON COLUMN tb_vacina.id IS 'Identificador interno da dose de vacina';
COMMENT ON COLUMN tb_vacina.pet_id IS 'Pet vacinado (FK tb_pet)';
COMMENT ON COLUMN tb_vacina.nome IS 'Nome/tipo da vacina aplicada';
COMMENT ON COLUMN tb_vacina.data_aplicacao IS 'Data em que a dose foi aplicada';
COMMENT ON COLUMN tb_vacina.proxima_dose IS 'Data prevista da próxima dose, usada para calcular o status';
COMMENT ON COLUMN tb_vacina.lote IS 'Lote do imunizante aplicado';
COMMENT ON COLUMN tb_vacina.veterinario IS 'Veterinário responsável pela aplicação';
COMMENT ON COLUMN tb_vacina.criado_em IS 'Data e hora de criação do registro';
-- =====================================================================
-- V3 - Índices de performance
-- =====================================================================

CREATE INDEX idx_tutor_firebase_uid ON tb_tutor (firebase_uid);
CREATE INDEX idx_pet_tutor_id       ON tb_pet (tutor_id);
CREATE INDEX idx_consulta_pet_id    ON tb_consulta (pet_id);
CREATE INDEX idx_consulta_tutor_id  ON tb_consulta (tutor_id);
CREATE INDEX idx_consulta_data      ON tb_consulta (data);
CREATE INDEX idx_vacina_pet_id      ON tb_vacina (pet_id);
