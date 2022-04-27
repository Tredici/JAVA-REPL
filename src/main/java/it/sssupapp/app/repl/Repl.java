
package it.sssupapp.app.repl;

import it.sssupapp.app.repl.CommandManager.ReadyToExecute;
import it.sssupapp.app.repl.annotations.AfterAll;
import it.sssupapp.app.repl.annotations.AfterEach;
import it.sssupapp.app.repl.annotations.BeforeAll;
import it.sssupapp.app.repl.annotations.BeforeEach;
import it.sssupapp.app.repl.annotations.Command;
import it.sssupapp.app.repl.exceptions.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

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

    private List<Supplier<Exception>> beforeAll = new ArrayList<>();
    private List<Supplier<Exception>> afterAll = new ArrayList<>();
    private List<Supplier<Exception>> beforeEach = new ArrayList<>();
    private List<Supplier<Exception>> afterEach = new ArrayList<>();

    private Exception execList(List<Supplier<Exception>> l) {
        for (var supplier : l) {
            var e = supplier.get();
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    private void execBeforeAll() throws BeforeAllException {
        var e = execList(beforeAll);
        if (e != null) {
            throw new BeforeAllException(e);
        }
    }

    private void execAfterAll() throws AfterAllException {
        var e = execList(afterAll);
        if (e != null) {
            throw new AfterAllException(e);
        }
    }

    private void execBeforeEach() throws BeforeEachException {
        var e = execList(beforeEach);
        if (e != null) {
            throw new BeforeEachException(e);
        }
    }

    private void execAfterEach() throws AfterEachException {
        var e = execList(afterEach);
        if (e != null) {
            throw new AfterEachException(e);
        }
    }

    private Supplier<Exception> packHandler(Object obj, Method method)
    {
        return () -> {
            try {
                method.invoke(obj);
                return null;
            } catch (Exception e) {
                return e;
            }
        };
    }

    public Repl(ReplConfig config) throws Exception
    {
        this.config = config;
        this.reader = new Scanner(System.in);
        this.cmds = new HashMap<>();

        var obj = config.getCommandObject();
        // aggiunge i nomi dei comandi riconosciuti
        for (var cmd : config.getCommandObject().getClass().getMethods()) {
            if (cmd.isAnnotationPresent(Command.class)) {
                var cmdName = cmd.getName();
                addCommandHandler(cmdName, obj, cmd);
            }
            else if (cmd.isAnnotationPresent(BeforeAll.class)) {
                this.beforeAll.add(packHandler(obj, cmd));
            }
            else if (cmd.isAnnotationPresent(AfterAll.class)) {
                this.afterAll.add(packHandler(obj, cmd));
            }
            else if (cmd.isAnnotationPresent(BeforeEach.class)) {
                this.beforeEach.add(packHandler(obj, cmd));
            }
            else if (cmd.isAnnotationPresent(AfterEach.class)) {
                this.afterEach.add(packHandler(obj, cmd));
            }
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

    public ReplStatus run() throws UnknownReplStatusException, ReplException
    {
        ReplStatus ans = ReplStatus.Continue;
        boolean exit = false;

        execBeforeAll();
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

                execBeforeEach();
                
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
                                throw new UnknownReplStatusException("Unknowk: " + ans);
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
                    execAfterEach();
                }
            } while (!exit);
        }
        finally
        {
            execAfterAll();
        }
        return ans;
    }
    
}