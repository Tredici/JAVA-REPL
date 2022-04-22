package it.sssupapp.app.repl;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class will be used to handle a set a commands.
 * All these command should be identified by the same method name.
 */
public class CommandManager {

    public static String usage(Method mthd)
    {
        var params = mthd.getParameters();
        var str = new String[params.length];
        for (int i=0; i != params.length; ++i)
        {
            var m = params[i];
            str[i] = m.getName()+":"+m.getType().getSimpleName();
        } 
        String args = String.join(" ", str);
        return  mthd.getName() + " " + args; 
    }

    public String[] getHelp()
    {
        String[] ans = new String[this.methods.size()];
        int i=0;
        for (var mthd : this.methods.values()) {
            ans[i++] = usage(mthd.method);
        }
        return ans;
    }

    public class ReadyToExecute
    {
        Object base;
        Method method;
        Object[] args;
    }

    class CmdPair
    {
        public Object base;
        public Method method;
    }

    private String cmdName;
    private Map<Integer,CmdPair> methods;

    public CommandManager(String name)
    {
        cmdName = name;
        methods = new HashMap<>();
    }

    public String getName()
    {
        return cmdName;
    }

    public void addMethod(Object base, Method method) throws Exception
    {
        // how many args required?
        int paramCount = method.getParameterCount();
        // check if there are other registered methods with the same numbers of args
        if (this.methods.get(paramCount) != null)
        {
            throw new Exception("Ci sono già metodi con " + paramCount + " argomenti");
        }
        var pair = new CmdPair();
        pair.base = base; pair.method = method;
        method.setAccessible(true); // to avoid VM restrictions
        this.methods.put(paramCount, pair);
    }

    // prepare arguments to be passed to the handler method 
    private Object[] castArgs(Class<?>[] argTypes, String[] args) throws Exception
    {
        var ans = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; ++i)
        {
            try
            {
                if (String.class.equals(argTypes[i]))
                {
                    ans[i] = args[i];
                }
                else if (Integer.class.equals(argTypes[i]))
                {
                    ans[i] = Integer.valueOf(args[i]);
                }
                else
                {
                    throw new Exception("Unsupported type: " + argTypes[i]);
                }
            }
            catch (NumberFormatException e)
            {
                throw new Exception("Cannot cast argument", e);
            }
        }
        return ans;
    }

    /**
     * @param args
     *  should be the list (eventually empty) of args passed from the cli
     */
    public ReadyToExecute prepare(String[] args) throws Exception
    {
        if (args == null)
        {
            args = new String[0];
        }
        int paramCount = args.length;
        var pair = this.methods.get(paramCount);
        if (pair == null)
        {
            throw new Exception("No handler found for " + paramCount + " arguments");
        }

        var castedArgs = castArgs(pair.method.getParameterTypes(), args);
        var ans = new ReadyToExecute();
        ans.base = pair.base;
        ans.method = pair.method;
        ans.args = castedArgs;
        return ans;
    }

    public Object invoke(ReadyToExecute args) throws Exception
    {
        if (args == null)
        {
            throw new Exception("Missing parameter");
        }
        return args.method.invoke(args.base, args.args);
    }

    public Object prepareAndInvoke(String[] args) throws Exception
    {
        return invoke(prepare(args));
    }
}
