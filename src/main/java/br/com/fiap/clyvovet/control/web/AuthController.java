package br.com.fiap.clyvovet.control.web;

import br.com.fiap.clyvovet.dto.request.CadastroRequest;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Telas de autenticação do frontend web (Thymeleaf). O /login em si é servido
 * pelo Spring Security (loginPage), então aqui só garantimos o GET da view.
 */
@Controller
public class AuthController {

    private final TutorService tutorService;

    public AuthController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String formularioCadastro(Model model) {
        if (!model.containsAttribute("cadastroRequest")) {
            model.addAttribute("cadastroRequest", new CadastroRequest("", "", "", "", null));
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastroRequest") CadastroRequest request,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "cadastro";
        }
        try {
            tutorService.cadastrar(request);
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "cadastro";
        }
        model.addAttribute("sucesso", "Conta criada com sucesso! Faça login para continuar.");
        model.addAttribute("cadastroRequest", new CadastroRequest("", "", "", "", null));
        return "cadastro";
    }
}
