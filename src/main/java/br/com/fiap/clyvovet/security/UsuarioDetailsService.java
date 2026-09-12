package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.TutorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService para o login web (Thymeleaf), lendo tb_tutor por e-mail.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final TutorRepository tutorRepository;

    public UsuarioDetailsService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Tutor tutor = tutorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return User.builder()
                .username(tutor.getEmail())
                .password(tutor.getSenhaHash())
                .roles(tutor.getPerfil().name())
                .build();
    }
}
