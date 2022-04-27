package it.sssupapp.app.repl.exceptions;

public class BeforeEachException extends ReplException {
    public BeforeEachException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeforeEachException(Throwable cause) {
        super(cause);
    }
}

