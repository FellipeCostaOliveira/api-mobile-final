package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.dto.response.ErroResponse;
import br.com.fiap.clyvovet.service.TutorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Valida o Bearer token do Firebase em toda requisição para /api/v1/**.
 * No primeiro acesso de um uid ainda não conhecido, provisiona o Tutor (just-in-time).
 */
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenFilter.class);

    private final TutorService tutorService;

    /**
     * Este ObjectMapper é instanciado manualmente (o filtro não é um @Bean do
     * Spring, então não recebe o ObjectMapper autoconfigurado por injeção).
     * Por isso precisa registrar o JavaTimeModule explicitamente -- sem isso,
     * serializar o campo `timestamp` (LocalDateTime) de ErroResponse lança
     * InvalidDefinitionException, a exceção escapa do filtro, cai fora do
     * securityMatcher da API e o Spring acaba respondendo com o redirect de
     * login da cadeia web -- foi exatamente esse o efeito colateral observado.
     */
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public FirebaseTokenFilter(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            responderNaoAutorizado(response, "Token ausente");
            return;
        }

        String idToken = header.substring("Bearer ".length());

        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decoded.getUid();

            tutorService.buscarOuProvisionar(uid, decoded.getName(), decoded.getEmail());

            FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(uid);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.warn("Falha ao validar token do Firebase: {}", ex.getMessage());
            responderNaoAutorizado(response, "Token inválido ou expirado");
        }
    }

    private void responderNaoAutorizado(HttpServletResponse response, String mensagem) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ErroResponse erro = ErroResponse.de(401, "Sua sessão expirou", mensagem);
        response.getWriter().write(objectMapper.writeValueAsString(erro));
    }
}
