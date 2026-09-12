package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.exception.AcessoNegadoException;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.PetMapper;
import br.com.fiap.clyvovet.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final TutorService tutorService;
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, TutorService tutorService, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
        this.petMapper = petMapper;
    }

    /**
     * GET /api/v1/pets?tutorId={uid}. Nunca lança 404 — devolve lista vazia se o tutor
     * não tiver pets. O parâmetro tutorId da query é ignorado por segurança: sempre
     * listamos os pets do tutor autenticado (uidToken), não de quem quer que o cliente peça.
     */
    public List<Pet> listarDoTutorAutenticado(String uidToken) {
        return petRepository.findByTutorFirebaseUid(uidToken);
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
    }

    /**
     * Garante que o pet pertence ao tutor autenticado antes de expor/alterar o recurso.
     */
    public Pet buscarPorIdDoTutor(Long id, String uidToken) {
        return petRepository.findByIdAndTutorFirebaseUid(id, uidToken)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
    }

    @Transactional
    public Pet criar(PetRequest request, String uidToken) {
        Tutor tutor = tutorService.buscarPorFirebaseUid(uidToken);
        Pet pet = petMapper.paraEntidade(request, tutor);
        Pet salvo = petRepository.save(pet);
        log.info("Pet criado id={} tutor={}", salvo.getId(), uidToken);
        return salvo;
    }

    @Transactional
    public Pet atualizar(Long id, PetRequest request, String uidToken) {
        Pet pet = buscarPorId(id);
        validarPertenceAoTutor(pet, uidToken);
        petMapper.atualizarEntidade(pet, request);
        return petRepository.save(pet);
    }

    @Transactional
    public void excluir(Long id, String uidToken) {
        Pet pet = buscarPorId(id);
        validarPertenceAoTutor(pet, uidToken);
        petRepository.delete(pet);
        log.info("Pet excluído id={} tutor={}", id, uidToken);
    }

    private void validarPertenceAoTutor(Pet pet, String uidToken) {
        if (!pet.getTutor().getFirebaseUid().equals(uidToken)) {
            throw new AcessoNegadoException("Este pet não pertence ao tutor autenticado");
        }
    }
}
