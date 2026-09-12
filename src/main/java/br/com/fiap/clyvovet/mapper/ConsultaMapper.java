package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.dto.request.ConsultaRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ConsultaMapper {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    public Consulta paraEntidade(ConsultaRequest request, Tutor tutor, Pet pet) {
        Consulta consulta = new Consulta();
        consulta.setTutor(tutor);
        consulta.setPet(pet);
        consulta.setData(request.data());
        consulta.setHorario(request.horario());
        consulta.setClinica(request.clinica());
        consulta.setVeterinario(request.veterinario());
        consulta.setMotivo(request.motivo());
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(request.observacoes());
        return consulta;
    }

    public void atualizarEntidade(Consulta consulta, ConsultaRequest request, Pet pet) {
        consulta.setPet(pet);
        consulta.setData(request.data());
        consulta.setHorario(request.horario());
        consulta.setClinica(request.clinica());
        consulta.setVeterinario(request.veterinario());
        consulta.setMotivo(request.motivo());
        consulta.setObservacoes(request.observacoes());
        if (request.status() != null && !request.status().isBlank()) {
            consulta.setStatus(StatusConsulta.fromValor(request.status()));
        }
    }

    public ConsultaResponse paraResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getTutor().getFirebaseUid(),
                String.valueOf(consulta.getPet().getId()),
                consulta.getPet().getNome(),
                consulta.getData().format(FORMATO_DATA),
                consulta.getHorario(),
                consulta.getClinica(),
                consulta.getVeterinario(),
                consulta.getMotivo(),
                consulta.getStatus().getValor(),
                consulta.getObservacoes()
        );
    }
}
