package br.com.fiap.clyvovet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean isolado propositalmente. TutorService depende de PasswordEncoder, e
 * SecurityConfig depende de TutorService — se o PasswordEncoder morasse dentro
 * de SecurityConfig, o Spring cairia num ciclo (SecurityConfig -> TutorService
 * -> PasswordEncoder -> SecurityConfig) e a aplicação não sobe.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
