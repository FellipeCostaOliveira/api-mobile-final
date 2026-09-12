-- =====================================================================
-- V2 - Carga inicial de dados
-- =====================================================================

INSERT INTO tb_tutor (firebase_uid, nome, email, senha_hash, telefone, perfil) VALUES
('uid-tutor-ana',     'Ana Beatriz Lima',       'ana.lima@example.com',     '$2a$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmnop', '11988887777', 'TUTOR'),
('uid-tutor-carlos',  'Carlos Eduardo Ramos',   'carlos.ramos@example.com','$2a$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmnop', '11977776666', 'TUTOR'),
('uid-tutor-mariana', 'Mariana Torres Alves',   'mariana.torres@example.com','$2a$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmnop', '11966665555', 'TUTOR'),
('uid-vet-marina',    'Dra. Marina Alves',      'marina.alves@clyvovet.com','$2a$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmnop', '11955554444', 'VETERINARIO'),
('uid-vet-roberto',   'Dr. Roberto Nunes',      'roberto.nunes@clyvovet.com','$2a$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmnop', '11944443333', 'VETERINARIO');

INSERT INTO tb_pet (tutor_id, nome, especie, raca, sexo, data_nascimento, peso, castrado, foto, observacoes) VALUES
(1, 'Thor',    'cachorro', 'Golden Retriever',  'Macho',  '2022-03-15', 28.40, TRUE,  '', 'Alérgico a frango'),
(1, 'Mimi',    'gato',     'Siamês',            'Fêmea',  '2021-07-02', 4.20,  TRUE,  '', ''),
(2, 'Rex',     'cachorro', 'Vira-lata',         'Macho',  '2019-11-20', 18.00, FALSE, '', 'Medo de fogos de artifício'),
(2, 'Loro',    'ave',      'Calopsita',         'Macho',  '2023-01-10', 0.10,  FALSE, '', ''),
(3, 'Nina',    'roedor',   'Hamster Sírio',     'Fêmea',  '2024-02-05', 0.15,  FALSE, '', 'Muito ativa à noite');

INSERT INTO tb_consulta (pet_id, tutor_id, data, horario, clinica, veterinario, motivo, status, observacoes) VALUES
(1, 1, '2026-09-24', '14:30', 'Clyvo Vet — Unidade Paulista',   'Dra. Marina Alves',  'Vacinação anual',         'agendada',  ''),
(2, 1, '2026-06-10', '10:00', 'Clyvo Vet — Unidade Paulista',   'Dr. Roberto Nunes',  'Check-up de rotina',      'concluida', 'Tudo normal'),
(3, 2, '2026-07-22', '09:15', 'Clyvo Vet — Unidade Moema',      'Dra. Marina Alves',  'Dor na pata traseira',    'concluida', 'Leve entorse'),
(4, 2, '2026-09-30', '16:00', 'Clyvo Vet — Unidade Moema',      'Dr. Roberto Nunes',  'Avaliação de bico',       'agendada',  ''),
(5, 3, '2026-05-18', '11:45', 'Clyvo Vet — Unidade Pinheiros',  'Dra. Marina Alves',  'Consulta cancelada pelo tutor', 'cancelada', 'Tutor remarcará');

INSERT INTO tb_prontuario (consulta_id, diagnostico, prescricao, peso_aferido, retorno_em, registrado_por) VALUES
(2, 'Animal saudável, sem alterações clínicas relevantes.', 'Manter dieta atual e atividade física regular.', 4.30, NULL, 'Dr. Roberto Nunes'),
(3, 'Entorse leve em pata traseira direita.', 'Repouso por 7 dias e anti-inflamatório conforme bula.', 18.20, '2026-08-05', 'Dra. Marina Alves');

INSERT INTO tb_vacina (pet_id, nome, data_aplicacao, proxima_dose, lote, veterinario) VALUES
(1, 'V10 (Múltipla canina)', '2025-09-20', '2026-09-20', 'LT-2025-081', 'Dra. Marina Alves'),
(1, 'Antirrábica',           '2025-10-05', '2026-10-05', 'LT-2025-092', 'Dra. Marina Alves'),
(2, 'V4 (Múltipla felina)',  '2025-08-15', '2026-08-15', 'LT-2025-070', 'Dr. Roberto Nunes'),
(3, 'Antirrábica',           '2025-06-01', '2026-06-01', 'LT-2025-050', 'Dra. Marina Alves'),
(4, 'Vacina aviária básica', '2025-11-10', '2026-11-10', 'LT-2025-110', 'Dr. Roberto Nunes');
