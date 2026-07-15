package sn.isepat.gestionetudiants.controller;

import sn.isepat.gestionetudiants.dto.AuthResponse;
import sn.isepat.gestionetudiants.dto.LoginRequest;
import sn.isepat.gestionetudiants.dto.RegisterRequest;
import sn.isepat.gestionetudiants.entity.Utilisateur;
import sn.isepat.gestionetudiants.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        Utilisateur utilisateur = authService.inscrire(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Authentification et generation du token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.authentifier(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}