package br.com.fiap.clyvovet.control.web;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.TutorService;
import br.com.fiap.clyvovet.service.VacinacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final TutorService tutorService;
    private final PetService petService;
    private final ConsultaService consultaService;
    private final VacinacaoService vacinacaoService;

    public DashboardController(TutorService tutorService, PetService petService, ConsultaService consultaService,
                                VacinacaoService vacinacaoService) {
        this.tutorService = tutorService;
        this.petService = petService;
        this.consultaService = consultaService;
        this.vacinacaoService = vacinacaoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());

        List<Pet> pets = petService.listarDoTutorAutenticado(tutor.getFirebaseUid());
        List<Consulta> consultas = consultaService.listarDoTutorAutenticado(tutor.getFirebaseUid());

        long agendadas = consultas.stream().filter(c -> c.getStatus() == StatusConsulta.AGENDADA).count();
        long concluidas = consultas.stream().filter(c -> c.getStatus() == StatusConsulta.CONCLUIDA).count();

        model.addAttribute("tutor", tutor);
        model.addAttribute("totalPets", pets.size());
        model.addAttribute("consultasAgendadas", agendadas);
        model.addAttribute("consultasConcluidas", concluidas);
        model.addAttribute("pendenciasVacina", vacinacaoService.totalPendenciasDoTutor(tutor.getId()));
        return "dashboard";
    }
}
