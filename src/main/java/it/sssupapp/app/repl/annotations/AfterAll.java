package it.sssupapp.app.repl.annotations;

import java.lang.annotation.*;

/**
 * Mark a method that must be executed once
 * when the repl loop terminate
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterAll {    
}
