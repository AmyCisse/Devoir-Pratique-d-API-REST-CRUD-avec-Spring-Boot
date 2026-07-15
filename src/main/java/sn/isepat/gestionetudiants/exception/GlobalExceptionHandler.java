package sn.isepat.gestionetudiants.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Email deja existant -> 409 Conflict
    @ExceptionHandler(EmailDejaExistantException.class)
    public ResponseEntity<ErrorResponse> gererEmailDejaExistant(EmailDejaExistantException ex) {
        ErrorResponse erreur = new ErrorResponse(409, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erreur);
    }

    // Mauvais email/mot de passe -> 401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> gererMauvaisIdentifiants(BadCredentialsException ex) {
        ErrorResponse erreur = new ErrorResponse(401, "Email ou mot de passe incorrect.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erreur);
    }
}