package br.com.fiap.clyvovet.control.web;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Especie;
import br.com.fiap.clyvovet.model.Sexo;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.request.VacinaRequest;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.TutorService;
import br.com.fiap.clyvovet.service.VacinacaoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pets")
public class PetWebController {

    private final PetService petService;
    private final TutorService tutorService;
    private final VacinacaoService vacinacaoService;

    public PetWebController(PetService petService, TutorService tutorService, VacinacaoService vacinacaoService) {
        this.petService = petService;
        this.tutorService = tutorService;
        this.vacinacaoService = vacinacaoService;
    }

    @GetMapping("/{id}/vacinas")
    public String carteiraDeVacinacao(@PathVariable Long id, Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        Pet pet = petService.buscarPorIdDoTutor(id, tutor.getFirebaseUid());
        List<Vacina> doses = vacinacaoService.carteiraDoPet(id);

        model.addAttribute("pet", pet);
        model.addAttribute("doses", doses);
        model.addAttribute("vacinacaoService", vacinacaoService);
        model.addAttribute("vacinaRequest", new VacinaRequest("", LocalDate.now(), "", ""));
        return "pets/vacinas";
    }

    @PostMapping("/{id}/vacinas")
    public String registrarDose(@PathVariable Long id, @Valid @ModelAttribute("vacinaRequest") VacinaRequest request,
                                 BindingResult bindingResult, Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        Pet pet = petService.buscarPorIdDoTutor(id, tutor.getFirebaseUid());

        if (bindingResult.hasErrors()) {
            model.addAttribute("pet", pet);
            model.addAttribute("doses", vacinacaoService.carteiraDoPet(id));
            model.addAttribute("vacinacaoService", vacinacaoService);
            return "pets/vacinas";
        }

        vacinacaoService.registrarDose(id, request);
        return "redirect:/pets/{id}/vacinas";
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        model.addAttribute("pets", petService.listarDoTutorAutenticado(tutor.getFirebaseUid()));
        return "pets/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("modo", "novo");
        model.addAttribute("petRequest", new PetRequest(null, "", null, "", null, null, "", false, "", ""));
        adicionarEnums(model);
        return "pets/form";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Authentication authentication, Model model) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        Pet pet = petService.buscarPorIdDoTutor(id, tutor.getFirebaseUid());

        PetRequest request = new PetRequest(
                tutor.getFirebaseUid(), pet.getNome(), pet.getEspecie(), pet.getRaca(), pet.getSexo(),
                pet.getDataNascimento(), pet.getPeso() == null ? "" : pet.getPeso().toString(),
                pet.isCastrado(), pet.getFoto(), pet.getObservacoes()
        );

        model.addAttribute("modo", "editar");
        model.addAttribute("petId", id);
        model.addAttribute("petRequest", request);
        adicionarEnums(model);
        return "pets/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("petRequest") PetRequest request, BindingResult bindingResult,
                         Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modo", "novo");
            adicionarEnums(model);
            return "pets/form";
        }
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        petService.criar(request, tutor.getFirebaseUid());
        return "redirect:/pets";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("petRequest") PetRequest request,
                             BindingResult bindingResult, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("petId", id);
            adicionarEnums(model);
            return "pets/form";
        }
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        petService.atualizar(id, request, tutor.getFirebaseUid());
        return "redirect:/pets";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, Authentication authentication) {
        Tutor tutor = tutorService.buscarPorEmail(authentication.getName());
        petService.excluir(id, tutor.getFirebaseUid());
        return "redirect:/pets";
    }

    private void adicionarEnums(Model model) {
        model.addAttribute("especies", Especie.values());
        model.addAttribute("sexos", Sexo.values());
    }
}
