package it.sssupapp.app.repl.annotations;

import java.lang.annotation.*;

/**
 * Mark a method that should be executed before
 * every command
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeEach {
}
