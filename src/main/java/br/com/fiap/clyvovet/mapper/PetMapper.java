package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class PetMapper {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    public Pet paraEntidade(PetRequest request, Tutor tutor) {
        Pet pet = new Pet();
        pet.setTutor(tutor);
        pet.setNome(request.nome());
        pet.setEspecie(request.especie());
        pet.setRaca(request.raca());
        pet.setSexo(request.sexo());
        pet.setDataNascimento(request.dataNascimento());
        pet.setPeso(paraPeso(request.peso()));
        pet.setCastrado(request.castrado());
        pet.setFoto(request.foto() == null ? "" : request.foto());
        pet.setObservacoes(request.observacoes());
        return pet;
    }

    public void atualizarEntidade(Pet pet, PetRequest request) {
        pet.setNome(request.nome());
        pet.setEspecie(request.especie());
        pet.setRaca(request.raca());
        pet.setSexo(request.sexo());
        pet.setDataNascimento(request.dataNascimento());
        pet.setPeso(paraPeso(request.peso()));
        pet.setCastrado(request.castrado());
        pet.setFoto(request.foto() == null ? "" : request.foto());
        pet.setObservacoes(request.observacoes());
    }

    public PetResponse paraResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getTutor().getFirebaseUid(),
                pet.getNome(),
                pet.getEspecie().getValor(),
                pet.getRaca(),
                pet.getSexo().getValor(),
                pet.getDataNascimento() == null ? null : pet.getDataNascimento().format(FORMATO_DATA),
                pet.getPeso(),
                pet.isCastrado(),
                pet.getFoto(),
                pet.getObservacoes()
        );
    }

    private BigDecimal paraPeso(String peso) {
        if (peso == null || peso.isBlank()) {
            return null;
        }
        return new BigDecimal(peso.replace(",", "."));
    }
}
