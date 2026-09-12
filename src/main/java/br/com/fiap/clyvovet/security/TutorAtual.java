package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.exception.AcessoNegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilitário para obter o firebaseUid do tutor autenticado na requisição atual.
 * Usado pelos controllers/services da API para nunca confiar no tutorId do corpo.
 */
@Component
public class TutorAtual {

    public String firebaseUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof FirebaseAuthenticationToken token) {
            return token.getFirebaseUid();
        }
        throw new AcessoNegadoException("Requisição não autenticada via Firebase");
    }
}
