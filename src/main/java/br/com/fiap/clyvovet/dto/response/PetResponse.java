package br.com.fiap.clyvovet.dto.response;

import java.math.BigDecimal;

/**
 * Resposta de /api/v1/pets. "id" fica no nível raiz (o app converte para string).
 * "dataNascimento" sempre como string yyyy-MM-dd (nunca ISO com hora).
 */
public record PetResponse(
        Long id,
        String tutorId,
        String nome,
        String especie,
        String raca,
        String sexo,
        String dataNascimento,
        BigDecimal peso,
        boolean castrado,
        String foto,
        String observacoes
) {
}
