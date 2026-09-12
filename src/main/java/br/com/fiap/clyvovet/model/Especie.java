package br.com.fiap.clyvovet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Espécie do pet. O valor trafega em minúsculo no JSON da API,
 * exatamente como o app mobile envia e espera de volta.
 */
public enum Especie {
    CACHORRO("cachorro"),
    GATO("gato"),
    AVE("ave"),
    ROEDOR("roedor"),
    OUTRO("outro");

    private final String valor;

    Especie(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Especie fromValor(String valor) {
        for (Especie especie : values()) {
            if (especie.valor.equalsIgnoreCase(valor)) {
                return especie;
            }
        }
        throw new IllegalArgumentException("Espécie inválida: " + valor);
    }
}
