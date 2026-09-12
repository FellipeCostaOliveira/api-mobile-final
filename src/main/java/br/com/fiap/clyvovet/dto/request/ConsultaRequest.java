package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Corpo enviado pelo app mobile em POST/PUT de /api/v1/consultas.
 * "tutorId" é descartado (vem do token). "petNome" é preenchido pelo backend a partir do pet.
 * "petId" chega como string e é convertido para Long no service.
 */
public record ConsultaRequest(
        String tutorId,

        @NotBlank(message = "O campo petId é obrigatório")
        String petId,

        String petNome,

        @NotNull(message = "O campo data é obrigatório")
        LocalDate data,

        @NotBlank(message = "O campo horario é obrigatório")
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d", message = "O horário deve estar no formato HH:mm")
        String horario,

        @NotBlank(message = "O campo clinica é obrigatório")
        String clinica,

        String veterinario,

        @NotBlank(message = "O campo motivo é obrigatório")
        String motivo,

        String status,

        String observacoes
) {
}
