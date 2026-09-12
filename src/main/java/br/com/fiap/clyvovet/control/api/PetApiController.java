package br.com.fiap.clyvovet.control.api;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.mapper.PetMapper;
import br.com.fiap.clyvovet.security.TutorAtual;
import br.com.fiap.clyvovet.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consumido pelo app mobile (React Native / Expo). Contrato imutável — ver
 * PARTE 1 do prompt original. Nunca acessa o Repository direto: sempre via Service.
 */
@RestController
@RequestMapping("/api/v1/pets")
public class PetApiController {

    private final PetService petService;
    private final PetMapper petMapper;
    private final TutorAtual tutorAtual;

    public PetApiController(PetService petService, PetMapper petMapper, TutorAtual tutorAtual) {
        this.petService = petService;
        this.petMapper = petMapper;
        this.tutorAtual = tutorAtual;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> listar(@RequestParam(required = false) String tutorId) {
        // O tutorId da query é ignorado por segurança: sempre listamos do uid do token.
        List<Pet> pets = petService.listarDoTutorAutenticado(tutorAtual.firebaseUid());
        return ResponseEntity.ok(pets.stream().map(petMapper::paraResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> buscar(@PathVariable Long id) {
        Pet pet = petService.buscarPorIdDoTutor(id, tutorAtual.firebaseUid());
        return ResponseEntity.ok(petMapper.paraResponse(pet));
    }

    @PostMapping
    public ResponseEntity<PetResponse> criar(@Valid @RequestBody PetRequest request) {
        Pet criado = petService.criar(request, tutorAtual.firebaseUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(petMapper.paraResponse(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PetRequest request) {
        Pet atualizado = petService.atualizar(id, request, tutorAtual.firebaseUid());
        return ResponseEntity.ok(petMapper.paraResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        petService.excluir(id, tutorAtual.firebaseUid());
        return ResponseEntity.noContent().build();
    }
}
