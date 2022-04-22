package it.sssupapp.app.repl;

import java.util.function.*;

public class Command {
    private String name;
    private String description;
    private Function<String[], ReplStatus> handler;

    public Command(String name, Function<String[], ReplStatus> fun)
    {
        assert name != null && fun != null && name.split("\\s").length != 1;
        this.name = name.toLowerCase();
        this.handler = fun;
    }
    public Command(String name, Function<String[], ReplStatus> fun, String description)
    {
        this(name, fun);
        assert description != null;
        this.description = description;
    }

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public Function<String[], ReplStatus> getHandler() { return this.handler; }

    public ReplStatus invoke(String[] args)
    {
        assert args != null;
        return this.handler.apply(args);
    }
}
