package br.com.fiap.clyvovet.control.api;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.dto.request.ConsultaRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.mapper.ConsultaMapper;
import br.com.fiap.clyvovet.security.TutorAtual;
import br.com.fiap.clyvovet.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consumido pelo app mobile. Mesmos cinco verbos e semântica de /pets.
 */
@RestController
@RequestMapping("/api/v1/consultas")
public class ConsultaApiController {

    private final ConsultaService consultaService;
    private final ConsultaMapper consultaMapper;
    private final TutorAtual tutorAtual;

    public ConsultaApiController(ConsultaService consultaService, ConsultaMapper consultaMapper, TutorAtual tutorAtual) {
        this.consultaService = consultaService;
        this.consultaMapper = consultaMapper;
        this.tutorAtual = tutorAtual;
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> listar(@RequestParam(required = false) String tutorId) {
        List<Consulta> consultas = consultaService.listarDoTutorAutenticado(tutorAtual.firebaseUid());
        return ResponseEntity.ok(consultas.stream().map(consultaMapper::paraResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscar(@PathVariable Long id) {
        Consulta consulta = consultaService.buscarPorIdDoTutor(id, tutorAtual.firebaseUid());
        return ResponseEntity.ok(consultaMapper.paraResponse(consulta));
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@Valid @RequestBody ConsultaRequest request) {
        Consulta criada = consultaService.criar(request, tutorAtual.firebaseUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaMapper.paraResponse(criada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaRequest request) {
        Consulta atualizada = consultaService.atualizar(id, request, tutorAtual.firebaseUid());
        return ResponseEntity.ok(consultaMapper.paraResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        consultaService.excluir(id, tutorAtual.firebaseUid());
        return ResponseEntity.noContent().build();
    }
}
