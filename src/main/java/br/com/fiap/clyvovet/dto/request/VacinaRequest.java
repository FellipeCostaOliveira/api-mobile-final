package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * Registro de uma nova dose de vacina (Fluxo B). A próxima dose é calculada
 * automaticamente pelo backend a partir do tipo de vacina — não é informada aqui.
 */
public record VacinaRequest(
        @NotBlank(message = "O nome da vacina é obrigatório")
        String nome,

        @NotNull(message = "A data de aplicação é obrigatória")
        @PastOrPresent(message = "A data de aplicação não pode ser no futuro")
        LocalDate dataAplicacao,

        String lote,

        String veterinario
) {
}
