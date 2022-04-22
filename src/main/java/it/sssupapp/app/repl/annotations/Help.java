package it.sssupapp.app.repl.annotations;

import java.lang.String;
import java.lang.annotation.*;


/**
 * This annotation will be used to associate to commands
 * a message to be displayed from the help function
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
    public String msg();
}
