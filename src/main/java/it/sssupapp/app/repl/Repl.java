
package it.sssupapp.app.repl;

import it.sssupapp.app.repl.CommandManager.ReadyToExecute;
import it.sssupapp.app.repl.annotations.Command;
import java.lang.reflect.Method;
import java.util.*;

public class Repl {

    ReplConfig config;
    Map<String,CommandManager> cmds;
    Scanner reader;

    private String[] parse(String cmdline)
    {
        String trimmed;
        return cmdline == null || (trimmed = cmdline.trim()).length() == 0 ? null : trimmed.split("\\s");
    }

    private void help()
    {
        System.out.println("Help: vedi i sorgenti.");
        System.out.println("I comandi disponibilie sono:");
        System.out.println("\thelp");
        for (var cmd : this.cmds.values())
        {
            System.out.println("\t" + cmd.getName() + ":");
            for (var h : cmd.getHelp()) {
                System.out.println("\t\t" + h);
            }
        }
    }

    private CommandManager getCmdManager(String cmdName)
    {
        var cmdManager = this.cmds.get(cmdName);
        if (cmdManager == null)
        {
            cmdManager = new CommandManager(cmdName);
            this.cmds.put(cmdName, cmdManager);
        }
        return cmdManager;
    }

    private void addCommandHandler(String cmdName, Object base, Method method) throws Exception
    {
        System.out.println("Inserting " + cmdName);
        var cmdMngr = getCmdManager(cmdName);

        if (method.getReturnType().isInstance(ReplStatus.class))
        {
            throw new Exception("Wrong return type: " + method.getReturnType().toString());
        }
        cmdMngr.addMethod(base, method);
    }

    public Repl(ReplConfig config) throws Exception
    {
        this.config = config;
        this.reader = new Scanner(System.in);
        this.cmds = new HashMap<>();

        // aggiunge i nomi dei comandi riconosciuti
        for (var cmd : config.getCommandObject().getClass().getMethods()) {
            if (!cmd.isAnnotationPresent(Command.class))
                continue;
            var cmdName = cmd.getName();
            addCommandHandler(cmdName, config.getCommandObject(), cmd);
        }
    }

    private String[] getInput()
    {
        System.out.print(">");
        var cmdline = this.reader.nextLine();
        var args = this.parse(cmdline);
        return args;
    }

    public CommandManager findCommand(String cmdName)
    {
        return this.cmds.get(cmdName);
    }

    public ReplStatus run() throws UnknownReplStatusException
    {
        ReplStatus ans = ReplStatus.Continue;
        boolean exit = false;

        if (config.beforeAll != null)
            config.beforeAll.get();
        try
        {
            do {
                var args = this.getInput();
                if (args == null)
                    continue;

                if (args[0].equals("help"))
                {
                    help();
                    continue;
                }

                var cmdMngr = findCommand(args[0]);
                if (cmdMngr == null)
                {
                    if (config.cmdNotFound != null)
                        config.cmdNotFound.accept(args[0]);
                    continue;
                }
                
                ReadyToExecute rte;
                try
                {
                    rte = cmdMngr.prepare(Arrays.copyOfRange(args, 1, args.length));
                    if (rte == null)
                        continue;
                }
                catch (Exception e)
                {
                    System.err.println(e);
                    System.err.println(e.getMessage());
                    continue;
                }

                if (config.before != null)
                    config.before.get();
                
                try
                {
                    try
                    {
                        ans = (ReplStatus) cmdMngr.invoke(rte);
                        switch (ans) {
                            case Continue:
                                break;
                            case NotFound:
                                System.err.println("Not found");
                            case Exit:
                                exit = true;
                                break;
                            default:
                                throw new UnknownReplStatusException();
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println("BUUUUM!");
                        System.err.println(e);
                        System.err.println(e.getMessage());
                        System.err.println(e.fillInStackTrace());
                    }
                }
                finally
                {
                    if (config.after != null)
                        config.after.get();
                }
            } while (!exit);
        }
        finally
        {
            if (config.afterAll != null)
                config.afterAll.get();
        }
        return ans;
    }
    
}