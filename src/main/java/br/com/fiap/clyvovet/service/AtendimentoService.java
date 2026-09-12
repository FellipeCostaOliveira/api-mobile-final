package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Prontuario;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.dto.request.ProntuarioRequest;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.ProntuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fluxo A — Atendimento veterinário. Em uma única transação: grava o prontuário,
 * conclui a consulta, atualiza o peso do pet e, se houver data de retorno,
 * cria automaticamente a próxima consulta (agendada). O tutor vê tudo refletido
 * no app mobile na hora, pois lê da mesma base via /api/v1/consultas e /api/v1/pets.
 */
@Service
public class AtendimentoService {

    private static final Logger log = LoggerFactory.getLogger(AtendimentoService.class);
    private static final String HORARIO_PADRAO_RETORNO = "09:00";

    private final ConsultaRepository consultaRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final PetRepository petRepository;

    public AtendimentoService(ConsultaRepository consultaRepository, ProntuarioRepository prontuarioRepository,
                               PetRepository petRepository) {
        this.consultaRepository = consultaRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public Prontuario registrarAtendimento(Long consultaId, ProntuarioRequest request, String nomeVeterinario) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraDeNegocioException("Não é possível concluir uma consulta cancelada");
        }
        if (consulta.getStatus() == StatusConsulta.CONCLUIDA) {
            throw new RegraDeNegocioException("Esta consulta já foi concluída");
        }

        Prontuario prontuario = new Prontuario();
        prontuario.setConsulta(consulta);
        prontuario.setDiagnostico(request.diagnostico());
        prontuario.setPrescricao(request.prescricao());
        prontuario.setPesoAferido(request.pesoAferido());
        prontuario.setRetornoEm(request.retornoEm());
        prontuario.setRegistradoPor(nomeVeterinario);
        prontuarioRepository.save(prontuario);

        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consultaRepository.save(consulta);

        Pet pet = consulta.getPet();
        pet.setPeso(request.pesoAferido());
        petRepository.save(pet);

        if (request.retornoEm() != null) {
            Consulta retorno = new Consulta();
            retorno.setTutor(consulta.getTutor());
            retorno.setPet(pet);
            retorno.setData(request.retornoEm());
            retorno.setHorario(HORARIO_PADRAO_RETORNO);
            retorno.setClinica(consulta.getClinica());
            retorno.setVeterinario(consulta.getVeterinario());
            retorno.setMotivo("Retorno — " + consulta.getMotivo());
            retorno.setStatus(StatusConsulta.AGENDADA);
            retorno.setObservacoes("Consulta de retorno gerada automaticamente após o atendimento de " + consulta.getData());
            consultaRepository.save(retorno);
            log.info("Retorno automático criado id={} para consulta original={}", retorno.getId(), consultaId);
        }

        log.info("Atendimento registrado consulta={} prontuario={}", consultaId, prontuario.getId());
        return prontuario;
    }
}
