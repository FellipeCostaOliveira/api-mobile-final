-- =====================================================================
-- V3 - Índices de performance
-- =====================================================================

CREATE INDEX idx_tutor_firebase_uid ON tb_tutor (firebase_uid);
CREATE INDEX idx_pet_tutor_id       ON tb_pet (tutor_id);
CREATE INDEX idx_consulta_pet_id    ON tb_consulta (pet_id);
CREATE INDEX idx_consulta_tutor_id  ON tb_consulta (tutor_id);
CREATE INDEX idx_consulta_data      ON tb_consulta (data);
CREATE INDEX idx_vacina_pet_id      ON tb_vacina (pet_id);
