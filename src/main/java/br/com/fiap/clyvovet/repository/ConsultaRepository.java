package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByTutorFirebaseUid(String firebaseUid);

    Optional<Consulta> findByIdAndTutorFirebaseUid(Long id, String firebaseUid);

    List<Consulta> findByTutorId(Long tutorId);

    List<Consulta> findByTutorIdAndStatus(Long tutorId, StatusConsulta status);

    List<Consulta> findByVeterinarioAndDataAndStatus(String veterinario, LocalDate data, StatusConsulta status);

    List<Consulta> findByDataAndStatus(LocalDate data, StatusConsulta status);
}
