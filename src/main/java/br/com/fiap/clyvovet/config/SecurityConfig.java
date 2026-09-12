package br.com.fiap.clyvovet.config;

import br.com.fiap.clyvovet.security.FirebaseTokenFilter;
import br.com.fiap.clyvovet.security.UsuarioDetailsService;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Duas cadeias de segurança independentes, nesta ORDEM (não inverter):
 *
 *   @Order(1) -> API_FILTRO_ORDEM  : /api/v1/**  -> stateless, token Firebase (app mobile)
 *   @Order(2) -> WEB_FILTRO_ORDEM  : demais rotas -> form login + sessão (Thymeleaf)
 *
 * O Spring avalia as SecurityFilterChain na ordem numérica do @Order. A primeira
 * cuja securityMatcher() casar com a requisição "vence" e as demais nem são
 * consultadas. Se @Order(1) e @Order(2) forem trocados (ou removidos), a cadeia
 * web -- que não tem securityMatcher e por isso responde a QUALQUER caminho --
 * intercepta as chamadas da API antes delas chegarem à cadeia certa, e o app
 * mobile passa a receber redirect 302 para /login em vez de 401 JSON. Foi
 * exatamente esse bug que quebrou a integração antes desta correção.
 *
 * A cadeia da API tem authenticationEntryPoint próprio (401 JSON) para nunca,
 * em hipótese alguma, cair no fluxo de formLogin da cadeia web.
 *
 * O PasswordEncoder é injetado a partir de PasswordEncoderConfig (bean isolado
 * em outro arquivo) -- não definir aqui dentro. Definir aqui reintroduz o ciclo
 * SecurityConfig -> TutorService -> PasswordEncoder -> SecurityConfig que
 * impedia a aplicação de subir (BeanCurrentlyInCreationException).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int API_FILTRO_ORDEM = 1;
    private static final int WEB_FILTRO_ORDEM = 2;

    private final TutorService tutorService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(TutorService tutorService,
                           UsuarioDetailsService usuarioDetailsService,
                           CorsConfigurationSource corsConfigurationSource,
                           PasswordEncoder passwordEncoder) {
        this.tutorService = tutorService;
        this.usuarioDetailsService = usuarioDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cadeia consumida pelo app mobile. Stateless: nada de sessão, nada de cookie.
     * AntPathRequestMatcher explícito evita qualquer ambiguidade de como o
     * Spring resolve o padrão da rota -- é o casamento mais literal possível.
     */
    @Bean
    @Order(API_FILTRO_ORDEM)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/api/v1/**"))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling
                        // Sem token válido -> 401 JSON. Nunca redirect para /login.
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("""
                                    {"status":401,"erro":"Nao autenticado","mensagem":"Token ausente ou invalido"}""");
                        })
                )
                .addFilterBefore(new FirebaseTokenFilter(tutorService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cadeia consumida pelo frontend Thymeleaf. Sem securityMatcher(): responde
     * a tudo que a cadeia da API não capturou (por isso precisa vir DEPOIS, em
     * @Order maior).
     */
    @Bean
    @Order(WEB_FILTRO_ORDEM)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/cadastro", "/css/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/vet/**").hasRole("VETERINARIO")
                        .requestMatchers("/pets/**", "/consultas/**", "/dashboard").hasAnyRole("TUTOR", "VETERINARIO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(handling -> handling.accessDeniedPage("/acesso-negado"));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
