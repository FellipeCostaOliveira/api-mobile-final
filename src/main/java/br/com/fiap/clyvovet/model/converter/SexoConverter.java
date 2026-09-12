package br.com.fiap.clyvovet.model.converter;

import br.com.fiap.clyvovet.model.Sexo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Mesma razão do EspecieConverter: persiste Sexo.getValor() ("Macho", "Fêmea")
 * em vez de Sexo.name() ("MACHO", "FEMEA"), que não bate com o CHECK
 * constraint da tabela tb_pet.
 */
@Converter(autoApply = true)
public class SexoConverter implements AttributeConverter<Sexo, String> {

    @Override
    public String convertToDatabaseColumn(Sexo sexo) {
        return sexo == null ? null : sexo.getValor();
    }

    @Override
    public Sexo convertToEntityAttribute(String valor) {
        return valor == null ? null : Sexo.fromValor(valor);
    }
}
