package sn.isepat.gestionetudiants.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.isepat.gestionetudiants.entity.Etudiant;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    boolean existsByMatricule(String matricule);

    boolean existsByEmail(String email);

    Optional<Etudiant> findByMatricule(String matricule);

    // Bonus : liste triée par nom (ordre alphabétique)
    List<Etudiant> findAllByOrderByNomAsc();
}
