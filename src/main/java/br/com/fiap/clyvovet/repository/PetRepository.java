package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * JOIN FETCH traz o Tutor na MESMA consulta SQL, dentro da única sessão do
     * Hibernate que esta chamada de repositório abre e fecha. Sem isso,
     * pet.getTutor() fica como um proxy "preguiçoso" que só pode ser acessado
     * enquanto essa sessão estiver aberta -- e como spring.jpa.open-in-view
     * está desligado (de propósito, é boa prática), a sessão fecha assim que
     * o repositório retorna, antes do PetMapper (chamado no controller) ler
     * pet.getTutor().getFirebaseUid(). O resultado é LazyInitializationException,
     * que o GlobalExceptionHandler devolve ao cliente como 500 genérico -- foi
     * exatamente o bug que quebrava GET /api/v1/pets/{id} (e quebraria também
     * a listagem, GET /api/v1/pets, assim que o tutor tivesse 1+ pet).
     */
    @Query("SELECT p FROM Pet p JOIN FETCH p.tutor WHERE p.tutor.firebaseUid = :firebaseUid")
    List<Pet> findByTutorFirebaseUid(@Param("firebaseUid") String firebaseUid);

    @Query("SELECT p FROM Pet p JOIN FETCH p.tutor WHERE p.id = :id AND p.tutor.firebaseUid = :firebaseUid")
    Optional<Pet> findByIdAndTutorFirebaseUid(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

    /**
     * Sobrescreve o findById padrão do JpaRepository com a mesma correção,
     * já que PetService.buscarPorId() (usado por atualizar/excluir) depende
     * dele. Redeclarar um método herdado com @Query é um recurso suportado
     * pelo Spring Data -- a query customizada tem prioridade sobre a padrão.
     */
    @Query("SELECT p FROM Pet p JOIN FETCH p.tutor WHERE p.id = :id")
    Optional<Pet> findById(@Param("id") Long id);

    List<Pet> findByTutorId(Long tutorId);
}
