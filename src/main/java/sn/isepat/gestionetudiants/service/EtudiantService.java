package sn.isepat.gestionetudiants.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.isepat.gestionetudiants.entity.Etudiant;
import sn.isepat.gestionetudiants.repository.EtudiantRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public List<Etudiant> findAll() {
        return etudiantRepository.findAll();
    }

    // Bonus : liste triée par nom
    public List<Etudiant> findAllTrieParNom() {
        return etudiantRepository.findAllByOrderByNomAsc();
    }

    public Optional<Etudiant> findById(Long id) {
        return etudiantRepository.findById(id);
    }

    // Bonus : recherche par matricule
    public Optional<Etudiant> findByMatricule(String matricule) {
        return etudiantRepository.findByMatricule(matricule);
    }

    public Etudiant save(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public Etudiant update(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public void delete(Long id) {
        etudiantRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return etudiantRepository.existsById(id);
    }

    public boolean matriculeExiste(String matricule) {
        return etudiantRepository.existsByMatricule(matricule);
    }

    public boolean emailExiste(String email) {
        return etudiantRepository.existsByEmail(email);
    }
}
