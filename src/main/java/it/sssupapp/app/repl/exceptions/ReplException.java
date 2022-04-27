package it.sssupapp.app.repl.exceptions;

import java.lang.Exception;

public class ReplException extends Exception {
    public ReplException(String message) {
        super(message);
    }

    public ReplException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplException(Throwable cause) {
        super(cause);
    }
}
