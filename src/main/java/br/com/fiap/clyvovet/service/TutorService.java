package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.dto.request.CadastroRequest;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.repository.TutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TutorService {

    private static final Logger log = LoggerFactory.getLogger(TutorService.class);

    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public TutorService(TutorRepository tutorRepository, PasswordEncoder passwordEncoder) {
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Usado pelo FirebaseTokenFilter: se o uid do token ainda não existe na base,
     * provisiona um Tutor automaticamente no primeiro acesso via API (mobile).
     */
    @Transactional
    public Tutor buscarOuProvisionar(String firebaseUid, String nome, String email) {
        return tutorRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    log.info("Provisionando novo tutor via Firebase, uid={}", firebaseUid);
                    String emailFinal = (email == null || email.isBlank())
                            ? firebaseUid + "@clyvovet.app"
                            : email;
                    Tutor novoTutor = new Tutor(
                            firebaseUid,
                            nome == null || nome.isBlank() ? "Tutor Clyvo Vet" : nome,
                            emailFinal,
                            passwordEncoder.encode(UUID.randomUUID().toString()),
                            null,
                            Perfil.TUTOR
                    );
                    return tutorRepository.save(novoTutor);
                });
    }

    public Tutor buscarPorFirebaseUid(String firebaseUid) {
        return tutorRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado para o uid informado"));
    }

    public Tutor buscarPorEmail(String email) {
        return tutorRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    @Transactional
    public Tutor cadastrar(CadastroRequest request) {
        if (tutorRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("Já existe uma conta com este e-mail");
        }
        Tutor tutor = new Tutor(
                "web-" + UUID.randomUUID(),
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()),
                request.telefone(),
                request.perfil()
        );
        return tutorRepository.save(tutor);
    }
}
