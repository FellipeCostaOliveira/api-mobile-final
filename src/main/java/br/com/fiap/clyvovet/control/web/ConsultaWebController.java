package br.com.fiap.clyvovet.control.web;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.dto.request.ConsultaRequest;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/consultas")
public class ConsultaWebController {

    private final ConsultaService consultaService;
    private final PetService petService;
    private final TutorService tutorService;

    public ConsultaWebController(ConsultaService consultaService, PetService petService, TutorService tutorService) {
        this.consultaService = consultaService;
        this.petService = petService;
        this.tutorService = tutorService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String status, Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        List<Consulta> consultas = consultaService.listarDoTutorAutenticado(tutor.getFirebaseUid());

        if (status != null && !status.isBlank()) {
            StatusConsulta filtro = StatusConsulta.fromValor(status);
            consultas = consultas.stream().filter(c -> c.getStatus() == filtro).toList();
        }

        model.addAttribute("consultas", consultas);
        model.addAttribute("statusAtual", status);
        return "consultas/lista";
    }

    @GetMapping("/nova")
    public String formularioNova(Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        model.addAttribute("pets", petService.listarDoTutorAutenticado(tutor.getFirebaseUid()));
        model.addAttribute("modo", "nova");
        model.addAttribute("consultaRequest", new ConsultaRequest(null, "", "", null, "", "", "", "", "agendada", ""));
        return "consultas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("consultaRequest") ConsultaRequest request, BindingResult bindingResult,
                         Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("pets", petService.listarDoTutorAutenticado(tutor.getFirebaseUid()));
            model.addAttribute("modo", "nova");
            return "consultas/form";
        }
        consultaService.criar(request, tutor.getFirebaseUid());
        return "redirect:/consultas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, Authentication authentication) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        Consulta consulta = consultaService.buscarPorIdDoTutor(id, tutor.getFirebaseUid());
        ConsultaRequest request = new ConsultaRequest(
                tutor.getFirebaseUid(), String.valueOf(consulta.getPet().getId()), consulta.getPet().getNome(),
                consulta.getData(), consulta.getHorario(), consulta.getClinica(), consulta.getVeterinario(),
                consulta.getMotivo(), "cancelada", consulta.getObservacoes()
        );
        consultaService.atualizar(id, request, tutor.getFirebaseUid());
        return "redirect:/consultas";
    }
}
