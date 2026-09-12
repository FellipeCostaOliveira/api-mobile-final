package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cadastro de novo usuário no frontend web (Thymeleaf).
 */
public record CadastroRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
        String senha,

        String telefone,

        @NotNull(message = "Selecione um perfil")
        Perfil perfil
) {
}
