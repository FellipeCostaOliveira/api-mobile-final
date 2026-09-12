package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Envelope padrão de erro devolvido por toda a API.
 */
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos,
        LocalDateTime timestamp
) {
    public static ErroResponse de(int status, String erro, String mensagem) {
        return new ErroResponse(status, erro, mensagem, Map.of(), LocalDateTime.now());
    }

    public static ErroResponse de(int status, String erro, String mensagem, Map<String, String> campos) {
        return new ErroResponse(status, erro, mensagem, campos, LocalDateTime.now());
    }
}
