package br.com.fiap.clyvovet.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Tratamento global de erros para o frontend Thymeleaf (fora de /api/v1/**).
 * Renderiza uma página amigável em vez do whitelabel padrão do Spring.
 */
@ControllerAdvice(basePackages = "br.com.fiap.clyvovet.control.web")
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public String tratarNaoEncontrado(RecursoNaoEncontradoException ex, Model model) {
        log.warn("Recurso não encontrado (web): {}", ex.getMessage());
        model.addAttribute("titulo", "Registro não encontrado");
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }

    @ExceptionHandler({AcessoNegadoException.class})
    public String tratarAcessoNegado(AcessoNegadoException ex, Model model) {
        log.warn("Acesso negado (web): {}", ex.getMessage());
        return "acesso-negado";
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public String tratarRegraDeNegocio(RegraDeNegocioException ex, Model model) {
        log.warn("Regra de negócio violada (web): {}", ex.getMessage());
        model.addAttribute("titulo", "Não foi possível concluir a ação");
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }
}
