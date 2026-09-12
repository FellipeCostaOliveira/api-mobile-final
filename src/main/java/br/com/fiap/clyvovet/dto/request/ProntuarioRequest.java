package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Preenchido pelo veterinário na tela de atendimento (Fluxo A, web/Thymeleaf).
 */
public record ProntuarioRequest(
        @NotBlank(message = "O diagnóstico é obrigatório")
        String diagnostico,

        String prescricao,

        @NotNull(message = "O peso aferido é obrigatório")
        @DecimalMin(value = "0.1", message = "O peso deve ser maior ou igual a 0.1 kg")
        @DecimalMax(value = "120", message = "O peso deve ser menor ou igual a 120 kg")
        BigDecimal pesoAferido,

        LocalDate retornoEm
) {
}
