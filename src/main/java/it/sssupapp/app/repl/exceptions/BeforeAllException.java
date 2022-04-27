package it.sssupapp.app.repl.exceptions;

public class BeforeAllException extends ReplException {
    public BeforeAllException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeforeAllException(Throwable cause) {
        super(cause);
    }
}
