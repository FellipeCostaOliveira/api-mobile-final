package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    Optional<Prontuario> findByConsultaId(Long consultaId);
}
