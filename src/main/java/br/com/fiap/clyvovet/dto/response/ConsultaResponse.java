package br.com.fiap.clyvovet.dto.response;

/**
 * Resposta de /api/v1/consultas. "id" no nível raiz, "data" como yyyy-MM-dd.
 */
public record ConsultaResponse(
        Long id,
        String tutorId,
        String petId,
        String petNome,
        String data,
        String horario,
        String clinica,
        String veterinario,
        String motivo,
        String status,
        String observacoes
) {
}
