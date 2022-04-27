
package it.sssupapp.app.repl.exceptions;

import it.sssupapp.app.repl.*;

public class UnknownReplStatusException extends ReplException {
    public UnknownReplStatusException(String msg) {
        super(msg);
    }

    public UnknownReplStatusException(ReplStatus msg) {
        super("Unknown status: " + msg);
    }
}
