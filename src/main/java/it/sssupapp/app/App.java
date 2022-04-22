package it.sssupapp.app;

import it.sssupapp.app.repl.*;
import it.sssupapp.app.repl.annotations.*;
import it.sssupapp.app.repl.annotations.Command;

/**
 * Hello world!
 *
 */
public class App 
{
    @Command
    public ReplStatus test()
    {
        System.out.println("Ciao!");
        return ReplStatus.Continue;
    }

    @Command
    public ReplStatus num(Integer str)
    {
        System.out.println("Hai inserito: " + str);
        return ReplStatus.Continue;
    }

    @Command
    public ReplStatus sum(Integer a, Integer b)
    {
        System.out.println("sum " + a + "+" + b + "=" + (a+b));
        return ReplStatus.Continue;
    }

    @Command
    public ReplStatus sum(Integer a, Integer b,  Integer c)
    {
        System.out.println("sum " + a + "+" + b + "+" + c + "=" + (a+b+c));
        return ReplStatus.Continue;
    }

    @Command
    public ReplStatus exit()
    {
        System.out.println("Addiooo!");
        return ReplStatus.Exit;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running test");

        var conf = new ReplConfig();
        conf.after = () -> { System.out.println("[<after>]"); return null; };
        conf.before = () -> { System.out.println("[<before>]"); return null; };
        conf.afterAll = () -> { System.out.println("[<afterAll>]"); return null; };
        conf.beforeAll = () -> { System.out.println("[<beforeAll>]"); return null; };
        conf.cmdNotFound = (cmd) -> { System.out.println("Unknown cmd \"" + cmd + "\", try \"help\"!"); };
        conf.commandObject = new App();

        try {
            new Repl(conf).run();
        } catch (Exception e) {
            System.out.println("È saltato tutto: " + e);
            throw e;
        }

        System.out.println("Test terminati");
    }
}
