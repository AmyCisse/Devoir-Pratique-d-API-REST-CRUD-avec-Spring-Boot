package sn.isepat.gestionetudiants.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sn.isepat.gestionetudiants.dto.LoginRequest;
import sn.isepat.gestionetudiants.dto.RegisterRequest;
import sn.isepat.gestionetudiants.entity.Utilisateur;
import sn.isepat.gestionetudiants.exception.EmailDejaExistantException;
import sn.isepat.gestionetudiants.repository.UtilisateurRepository;
import sn.isepat.gestionetudiants.security.JwtUtil;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UtilisateurRepository utilisateurRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Inscription : verifie l'unicite de l'email puis encode le mot de passe
    public Utilisateur inscrire(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new EmailDejaExistantException("Cet email existe déjà.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        // Le mot de passe est TOUJOURS enregistre encode, jamais en clair
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("USER");

        return utilisateurRepository.save(utilisateur);
    }

    // Authentification : verifie les identifiants puis genere un token JWT
    public String authentifier(LoginRequest request) {
        // Si l'email/mot de passe est incorrect, Spring Security leve BadCredentialsException
        // (interceptee par GlobalExceptionHandler -> 401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        return jwtUtil.genererToken(request.getEmail());
    }
}