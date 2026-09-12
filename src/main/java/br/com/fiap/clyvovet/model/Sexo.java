package br.com.fiap.clyvovet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Sexo do pet. Trafega como "Macho" ou "Fêmea" (com acento e maiúscula inicial).
 */
public enum Sexo {
    MACHO("Macho"),
    FEMEA("Fêmea");

    private final String valor;

    Sexo(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Sexo fromValor(String valor) {
        for (Sexo sexo : values()) {
            if (sexo.valor.equalsIgnoreCase(valor)) {
                return sexo;
            }
        }
        throw new IllegalArgumentException("Sexo inválido: " + valor);
    }
}
