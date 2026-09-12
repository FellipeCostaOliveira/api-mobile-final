package br.com.fiap.clyvovet.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Token de autenticação populado pelo FirebaseTokenFilter para as rotas /api/v1/**.
 * O "principal" é o firebaseUid, usado em todos os services da API como fonte da verdade
 * de quem é o tutor autenticado (nunca confiamos no tutorId do corpo da requisição).
 */
public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final String firebaseUid;

    public FirebaseAuthenticationToken(String firebaseUid) {
        super(List.of(new SimpleGrantedAuthority("ROLE_API_TUTOR")));
        this.firebaseUid = firebaseUid;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return firebaseUid;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }
}
