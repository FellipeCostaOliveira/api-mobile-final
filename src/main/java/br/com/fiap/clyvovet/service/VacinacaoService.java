package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.model.StatusVacina;
import br.com.fiap.clyvovet.dto.request.VacinaRequest;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fluxo B — Carteira de vacinação e alerta.
 * Classifica cada dose em EM_DIA / PROXIMA / ATRASADA a partir da data da próxima dose,
 * e calcula a próxima dose de uma nova aplicação pelo intervalo do tipo de vacina.
 */
@Service
public class VacinacaoService {

    private static final Logger log = LoggerFactory.getLogger(VacinacaoService.class);
    private static final int DIAS_LIMITE_PROXIMA = 30;
    private static final int INTERVALO_PADRAO_DIAS = 365;

    /** Intervalo (em dias) até a próxima dose, por tipo de vacina (busca por trecho do nome). */
    private static final Map<String, Integer> INTERVALOS_POR_TIPO = Map.of(
            "antirrábica", 365,
            "v10", 365,
            "v8", 365,
            "v4", 365,
            "aviária", 180,
            "giárdia", 180
    );

    private final VacinaRepository vacinaRepository;
    private final PetRepository petRepository;

    public VacinacaoService(VacinaRepository vacinaRepository, PetRepository petRepository) {
        this.vacinaRepository = vacinaRepository;
        this.petRepository = petRepository;
    }

    public StatusVacina classificar(Vacina vacina) {
        if (vacina.getProximaDose() == null) {
            return StatusVacina.EM_DIA;
        }
        LocalDate hoje = LocalDate.now();
        if (vacina.getProximaDose().isBefore(hoje)) {
            return StatusVacina.ATRASADA;
        }
        if (!vacina.getProximaDose().isAfter(hoje.plusDays(DIAS_LIMITE_PROXIMA))) {
            return StatusVacina.PROXIMA;
        }
        return StatusVacina.EM_DIA;
    }

    public List<Vacina> carteiraDoPet(Long petId) {
        return vacinaRepository.findByPetId(petId);
    }

    /**
     * Total de doses PROXIMA ou ATRASADA entre todos os pets do tutor — usado no dashboard.
     */
    public long totalPendenciasDoTutor(Long tutorId) {
        return vacinaRepository.findByPetTutorId(tutorId).stream()
                .filter(v -> classificar(v) != StatusVacina.EM_DIA)
                .count();
    }

    @Transactional
    public Vacina registrarDose(Long petId, VacinaRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));

        int intervaloDias = intervaloParaTipo(request.nome());
        LocalDate proximaDose = request.dataAplicacao().plusDays(intervaloDias);

        Vacina vacina = new Vacina();
        vacina.setPet(pet);
        vacina.setNome(request.nome());
        vacina.setDataAplicacao(request.dataAplicacao());
        vacina.setProximaDose(proximaDose);
        vacina.setLote(request.lote());
        vacina.setVeterinario(request.veterinario());

        Vacina salva = vacinaRepository.save(vacina);
        log.info("Dose de vacina registrada id={} pet={} proximaDose={}", salva.getId(), petId, proximaDose);
        return salva;
    }

    private int intervaloParaTipo(String nomeVacina) {
        String chaveBusca = nomeVacina.toLowerCase(Locale.ROOT);
        return INTERVALOS_POR_TIPO.entrySet().stream()
                .filter(entrada -> chaveBusca.contains(entrada.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(INTERVALO_PADRAO_DIAS);
    }
}
