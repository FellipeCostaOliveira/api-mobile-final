package br.com.fiap.clyvovet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status da consulta: agendada, concluida ou cancelada (minúsculo, sem acento).
 */
public enum StatusConsulta {
    AGENDADA("agendada"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada");

    private final String valor;

    StatusConsulta(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static StatusConsulta fromValor(String valor) {
        for (StatusConsulta status : values()) {
            if (status.valor.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de consulta inválido: " + valor);
    }
}
