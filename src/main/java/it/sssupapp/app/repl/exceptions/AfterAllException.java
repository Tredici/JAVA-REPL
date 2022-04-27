package it.sssupapp.app.repl.exceptions;

public class AfterAllException extends ReplException {
    public AfterAllException(String message, Throwable cause) {
        super(message, cause);
    }

    public AfterAllException(Throwable cause) {
        super(cause);
    }
}
