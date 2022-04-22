package it.sssupapp.app.repl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.LinkedList;

/**
 * This class will be used to collect all the
 * information available a certain command.
 *
 * These information will be used to 
 */
public class CommandDescription {
    /**
     * This class will be used to describe a particular
     * version of a command (1 version for each handler
     * method)
     */
    public class CommandVariationDescription
    {
        Method method;
        public CommandVariationDescription(Method method)
        {
            this.method = method;
        }

        public List<Parameter> getArguments()
        {
            List<Parameter> ans = new LinkedList<>();
            for (var par : method.getParameters()) {
                ans.add(par);
            }
            return ans;
        }

        public List<String> getArgumentList()
        {
            List<String> ans = new LinkedList<>();
            for (var par : this.getArguments()) {
                ans.add(par.getName()+":"+par.getType().getSimpleName());
            }
            return ans;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }
    }

    private String name;
    private List<CommandVariationDescription> variations;
    
    public CommandDescription(String name, Method[] methods)
    {
        if (methods == null || methods.length == 0)
        {
            throw new IllegalArgumentException("Invalid arguments!");
        }
        this.name = name;
        this.variations = new LinkedList<>();
        for (Method method : methods) {
            var cvd = new CommandVariationDescription(method);
            //TODO
        }
    }

    public String getName()
    {
        return name;
    }

    public int versions()
    {
        return variations.size();
    }
}
