package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.dto.request.ConsultaRequest;
import br.com.fiap.clyvovet.exception.AcessoNegadoException;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.mapper.ConsultaMapper;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TutorService tutorService;
    private final ConsultaMapper consultaMapper;

    public ConsultaService(ConsultaRepository consultaRepository, PetRepository petRepository,
                            TutorService tutorService, ConsultaMapper consultaMapper) {
        this.consultaRepository = consultaRepository;
        this.petRepository = petRepository;
        this.tutorService = tutorService;
        this.consultaMapper = consultaMapper;
    }

    public List<Consulta> listarDoTutorAutenticado(String uidToken) {
        return consultaRepository.findByTutorFirebaseUid(uidToken);
    }

    public Consulta buscarPorId(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));
    }

    public Consulta buscarPorIdDoTutor(Long id, String uidToken) {
        return consultaRepository.findByIdAndTutorFirebaseUid(id, uidToken)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));
    }

    @Transactional
    public Consulta criar(ConsultaRequest request, String uidToken) {
        Tutor tutor = tutorService.buscarPorFirebaseUid(uidToken);
        Pet pet = resolverPetDoTutor(request.petId(), tutor);

        if (request.data().isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("Não é possível agendar uma consulta no passado");
        }

        Consulta consulta = consultaMapper.paraEntidade(request, tutor, pet);
        Consulta salva = consultaRepository.save(consulta);
        log.info("Consulta criada id={} tutor={} pet={}", salva.getId(), uidToken, pet.getId());
        return salva;
    }

    @Transactional
    public Consulta atualizar(Long id, ConsultaRequest request, String uidToken) {
        Consulta consulta = buscarPorId(id);
        validarPertenceAoTutor(consulta, uidToken);

        Tutor tutor = consulta.getTutor();
        Pet pet = resolverPetDoTutor(request.petId(), tutor);

        consultaMapper.atualizarEntidade(consulta, request, pet);
        return consultaRepository.save(consulta);
    }

    @Transactional
    public void excluir(Long id, String uidToken) {
        Consulta consulta = buscarPorId(id);
        validarPertenceAoTutor(consulta, uidToken);
        consultaRepository.delete(consulta);
        log.info("Consulta excluída id={} tutor={}", id, uidToken);
    }

    /**
     * petId chega como string do app. Converte para Long e garante que o pet
     * existe e pertence ao tutor do token — senão 403, conforme contrato.
     */
    private Pet resolverPetDoTutor(String petIdTexto, Tutor tutor) {
        Long petId;
        try {
            petId = Long.valueOf(petIdTexto.trim());
        } catch (NumberFormatException ex) {
            throw new RegraDeNegocioException("petId inválido: " + petIdTexto);
        }

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado para petId=" + petId));

        if (!pet.getTutor().getId().equals(tutor.getId())) {
            throw new AcessoNegadoException("O pet informado não pertence ao tutor autenticado");
        }

        return pet;
    }

    private void validarPertenceAoTutor(Consulta consulta, String uidToken) {
        if (!consulta.getTutor().getFirebaseUid().equals(uidToken)) {
            throw new AcessoNegadoException("Esta consulta não pertence ao tutor autenticado");
        }
    }
}
