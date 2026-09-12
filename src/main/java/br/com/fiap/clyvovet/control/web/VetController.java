package br.com.fiap.clyvovet.control.web;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.dto.request.ProntuarioRequest;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.service.AtendimentoService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Rotas exclusivas do veterinário. Protegidas por hasRole("VETERINARIO") no SecurityConfig.
 */
@Controller
@RequestMapping("/vet")
public class VetController {

    private final ConsultaRepository consultaRepository;
    private final ConsultaService consultaService;
    private final AtendimentoService atendimentoService;
    private final TutorService tutorService;

    public VetController(ConsultaRepository consultaRepository, ConsultaService consultaService,
                          AtendimentoService atendimentoService, TutorService tutorService) {
        this.consultaRepository = consultaRepository;
        this.consultaService = consultaService;
        this.atendimentoService = atendimentoService;
        this.tutorService = tutorService;
    }

    @GetMapping("/agenda")
    public String agenda(Authentication authentication, Model model) {
        Tutor veterinario = tutorService.buscarPorEmail(authentication.getName());
        List<Consulta> agenda = consultaRepository.findByVeterinarioAndDataAndStatus(
                veterinario.getNome(), LocalDate.now(), StatusConsulta.AGENDADA);
        model.addAttribute("agenda", agenda);
        model.addAttribute("hoje", LocalDate.now());
        return "vet/agenda";
    }

    @GetMapping("/atendimento/{consultaId}")
    public String formularioAtendimento(@PathVariable Long consultaId, Model model) {
        Consulta consulta = consultaService.buscarPorId(consultaId);
        model.addAttribute("consulta", consulta);
        model.addAttribute("prontuarioRequest", new ProntuarioRequest("", "", BigDecimal.ZERO, null));
        return "vet/prontuario";
    }

    @PostMapping("/atendimento/{consultaId}")
    public String registrarAtendimento(@PathVariable Long consultaId,
                                        @Valid @ModelAttribute("prontuarioRequest") ProntuarioRequest request,
                                        BindingResult bindingResult,
                                        Authentication authentication,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("consulta", consultaService.buscarPorId(consultaId));
            return "vet/prontuario";
        }
        Tutor veterinario = tutorService.buscarPorEmail(authentication.getName());
        atendimentoService.registrarAtendimento(consultaId, request, veterinario.getNome());
        return "redirect:/vet/agenda";
    }
}
