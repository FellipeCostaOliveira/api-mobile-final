package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutorFirebaseUid(String firebaseUid);

    Optional<Pet> findByIdAndTutorFirebaseUid(Long id, String firebaseUid);

    List<Pet> findByTutorId(Long tutorId);
}
