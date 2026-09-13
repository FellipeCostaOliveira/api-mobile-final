package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /**
     * JOIN FETCH em tutor E pet: ConsultaMapper.paraResponse() acessa as duas
     * relações lazy (consulta.getTutor() e consulta.getPet()). Mesmo motivo
     * do PetRepository -- ver o comentário lá para a explicação completa do
     * porquê o fetch precisa acontecer na mesma consulta, e não depois.
     */
    @Query("SELECT c FROM Consulta c JOIN FETCH c.tutor JOIN FETCH c.pet WHERE c.tutor.firebaseUid = :firebaseUid")
    List<Consulta> findByTutorFirebaseUid(@Param("firebaseUid") String firebaseUid);

    @Query("SELECT c FROM Consulta c JOIN FETCH c.tutor JOIN FETCH c.pet "
            + "WHERE c.id = :id AND c.tutor.firebaseUid = :firebaseUid")
    Optional<Consulta> findByIdAndTutorFirebaseUid(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

    @Query("SELECT c FROM Consulta c JOIN FETCH c.tutor JOIN FETCH c.pet WHERE c.id = :id")
    Optional<Consulta> findById(@Param("id") Long id);

    List<Consulta> findByTutorId(Long tutorId);

    List<Consulta> findByTutorIdAndStatus(Long tutorId, StatusConsulta status);

    List<Consulta> findByVeterinarioAndDataAndStatus(String veterinario, LocalDate data, StatusConsulta status);

    List<Consulta> findByDataAndStatus(LocalDate data, StatusConsulta status);
}
