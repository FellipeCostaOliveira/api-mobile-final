package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.Especie;
import br.com.fiap.clyvovet.model.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corpo enviado pelo app mobile em POST/PUT de /api/v1/pets.
 * "tutorId" é aceito no JSON mas descartado pelo backend (o uid do token manda).
 * "peso" chega como string ou número — Jackson faz a coerção automaticamente para String.
 */
public record PetRequest(
        String tutorId,

        @NotBlank(message = "O campo nome é obrigatório")
        String nome,

        @NotNull(message = "O campo especie é obrigatório")
        Especie especie,

        String raca,

        @NotNull(message = "O campo sexo é obrigatório")
        Sexo sexo,

        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        String peso,

        boolean castrado,

        @Size(max = 500, message = "A foto deve ter no máximo 500 caracteres")
        String foto,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {
}
