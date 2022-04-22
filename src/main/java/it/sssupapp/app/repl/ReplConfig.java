
package it.sssupapp.app.repl;

import java.util.function.*;

public class ReplConfig {
    public ReplConfig() {}

    public Supplier<? extends Object> before;
    public Supplier<? extends Object> after;
    public Supplier<? extends Object> beforeAll;
    public Supplier<? extends Object> afterAll;

    public Consumer<String> cmdNotFound;

    public Object commandObject;
    public Object getCommandObject() { return this.commandObject; }
}
