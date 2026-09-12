package br.com.fiap.clyvovet.model.converter;

import br.com.fiap.clyvovet.model.StatusConsulta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Mesma razão dos conversores de Especie e Sexo: persiste
 * StatusConsulta.getValor() ("agendada", "concluida", "cancelada") em vez de
 * StatusConsulta.name() ("AGENDADA", "CONCLUIDA", "CANCELADA"), que não bate
 * com o CHECK constraint da tabela tb_consulta.
 */
@Converter(autoApply = true)
public class StatusConsultaConverter implements AttributeConverter<StatusConsulta, String> {

    @Override
    public String convertToDatabaseColumn(StatusConsulta status) {
        return status == null ? null : status.getValor();
    }

    @Override
    public StatusConsulta convertToEntityAttribute(String valor) {
        return valor == null ? null : StatusConsulta.fromValor(valor);
    }
}
