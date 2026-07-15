package sn.isepat.gestionetudiants.exception;

public class EmailDejaExistantException extends RuntimeException {

    public EmailDejaExistantException(String message) {
        super(message);
    }
}
