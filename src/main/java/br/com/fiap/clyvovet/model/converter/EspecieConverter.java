package br.com.fiap.clyvovet.model.converter;

import br.com.fiap.clyvovet.model.Especie;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Faz o Hibernate persistir Especie.getValor() ("cachorro", "gato"...) em vez
 * do nome do enum Java (Especie.name() = "CACHORRO"). Sem este conversor, o
 * Hibernate usaria o nome do enum por padrão -- que não bate com os valores
 * em minúsculo definidos no CHECK constraint da tabela tb_pet, e todo INSERT
 * de pet falha com ConstraintViolationException.
 *
 * autoApply = true: aplica automaticamente em qualquer atributo do tipo
 * Especie, sem precisar de @Convert em cada campo.
 */
@Converter(autoApply = true)
public class EspecieConverter implements AttributeConverter<Especie, String> {

    @Override
    public String convertToDatabaseColumn(Especie especie) {
        return especie == null ? null : especie.getValor();
    }

    @Override
    public Especie convertToEntityAttribute(String valor) {
        return valor == null ? null : Especie.fromValor(valor);
    }
}
