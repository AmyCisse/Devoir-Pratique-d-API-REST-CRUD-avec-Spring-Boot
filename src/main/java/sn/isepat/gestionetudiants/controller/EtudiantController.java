package sn.isepat.gestionetudiants.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.isepat.gestionetudiants.dto.ApiError;
import sn.isepat.gestionetudiants.entity.Etudiant;
import sn.isepat.gestionetudiants.service.EtudiantService;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST exposant les opérations CRUD sur les étudiants.
 * Les annotations de validation (@Valid, @NotBlank...) ne sont pas utilisées à ce stade :
 * tous les contrôles sont donc réalisés manuellement, avant tout ajout/modification.
 */
@RestController
@RequestMapping("/etudiants")
@RequiredArgsConstructor
@Tag(name = "Étudiants", description = "Gestion des étudiants de l'ISEP-AT (CRUD)")
public class EtudiantController {

    private final EtudiantService etudiantService;

    @PostMapping
    @Operation(summary = "Ajouter un étudiant",
            description = "Crée un étudiant après vérification des champs obligatoires et de l'unicité du matricule/email.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
            @ApiResponse(responseCode = "409", description = "Le matricule ou l'email existe déjà")
    })
    public ResponseEntity<Object> ajouter(@RequestBody Etudiant etudiant) {
        ApiError erreurChamp = validerChamps(etudiant);
        if (erreurChamp != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreurChamp);
        }
        if (etudiantService.matriculeExiste(etudiant.getMatricule())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "Le matricule existe déjà."));
        }
        if (etudiantService.emailExiste(etudiant.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "L'email existe déjà."));
        }
        Etudiant nouvelEtudiant = etudiantService.save(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelEtudiant);
    }

    @GetMapping
    @Operation(summary = "Lister les étudiants",
            description = "Retourne tous les étudiants. Utiliser tri=nom pour trier par nom (bonus).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste renvoyée avec succès")
    })
    public ResponseEntity<List<Etudiant>> lister(
            @Parameter(description = "Passer 'nom' pour trier par nom") @RequestParam(required = false) String tri) {
        if ("nom".equalsIgnoreCase(tri)) {
            return ResponseEntity.ok(etudiantService.findAllTrieParNom());
        }
        return ResponseEntity.ok(etudiantService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un étudiant par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<Object> rechercher(@PathVariable Long id) {
        Optional<Etudiant> etudiant = etudiantService.findById(id);
        if (etudiant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "Étudiant introuvable."));
        }
        return ResponseEntity.ok(etudiant.get());
    }

    @GetMapping("/matricule/{matricule}")
    @Operation(summary = "Rechercher un étudiant par matricule (bonus)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<Object> rechercherParMatricule(@PathVariable String matricule) {
        Optional<Etudiant> etudiant = etudiantService.findByMatricule(matricule);
        if (etudiant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "Étudiant introuvable."));
        }
        return ResponseEntity.ok(etudiant.get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un étudiant",
            description = "Met à jour un étudiant après vérification des champs obligatoires et de l'unicité.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable"),
            @ApiResponse(responseCode = "409", description = "Le matricule ou l'email appartient déjà à un autre étudiant")
    })
    public ResponseEntity<Object> modifier(@PathVariable Long id, @RequestBody Etudiant etudiantMaj) {
        Optional<Etudiant> etudiantExistantOpt = etudiantService.findById(id);
        if (etudiantExistantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "Étudiant introuvable."));
        }

        ApiError erreurChamp = validerChamps(etudiantMaj);
        if (erreurChamp != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreurChamp);
        }

        Etudiant etudiantExistant = etudiantExistantOpt.get();

        boolean matriculeChange = !etudiantExistant.getMatricule().equals(etudiantMaj.getMatricule());
        if (matriculeChange && etudiantService.matriculeExiste(etudiantMaj.getMatricule())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "Le matricule existe déjà."));
        }

        boolean emailChange = !etudiantExistant.getEmail().equals(etudiantMaj.getEmail());
        if (emailChange && etudiantService.emailExiste(etudiantMaj.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "L'email existe déjà."));
        }

        etudiantExistant.setMatricule(etudiantMaj.getMatricule());
        etudiantExistant.setPrenom(etudiantMaj.getPrenom());
        etudiantExistant.setNom(etudiantMaj.getNom());
        etudiantExistant.setEmail(etudiantMaj.getEmail());
        etudiantExistant.setDateNaissance(etudiantMaj.getDateNaissance());
        etudiantExistant.setLieuNaissance(etudiantMaj.getLieuNaissance());
        etudiantExistant.setNationalite(etudiantMaj.getNationalite());

        Etudiant etudiantModifie = etudiantService.update(etudiantExistant);
        return ResponseEntity.ok(etudiantModifie);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un étudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Étudiant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    public ResponseEntity<Object> supprimer(@PathVariable Long id) {
        if (!etudiantService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "Étudiant introuvable."));
        }
        etudiantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Contrôle manuel des champs obligatoires. Retourne la première erreur, ou null si tout est correct.
    private ApiError validerChamps(Etudiant etudiant) {
        if (etudiant.getMatricule() == null || etudiant.getMatricule().isBlank()) {
            return new ApiError(400, "Le matricule est obligatoire.");
        }
        if (etudiant.getPrenom() == null || etudiant.getPrenom().isBlank()) {
            return new ApiError(400, "Le prénom est obligatoire.");
        }
        if (etudiant.getNom() == null || etudiant.getNom().isBlank()) {
            return new ApiError(400, "Le nom est obligatoire.");
        }
        if (etudiant.getEmail() == null || etudiant.getEmail().isBlank()) {
            return new ApiError(400, "L'email est obligatoire.");
        }
        if (etudiant.getDateNaissance() == null) {
            return new ApiError(400, "La date de naissance est obligatoire.");
        }
        if (etudiant.getLieuNaissance() == null || etudiant.getLieuNaissance().isBlank()) {
            return new ApiError(400, "Le lieu de naissance est obligatoire.");
        }
        if (etudiant.getNationalite() == null || etudiant.getNationalite().isBlank()) {
            return new ApiError(400, "La nationalité est obligatoire.");
        }
        return null;
    }
}
