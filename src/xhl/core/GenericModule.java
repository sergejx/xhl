package xhl.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xhl.core.exceptions.EvaluationException;

public class GenericModule implements Module {

    protected final Evaluator evaluator;

    public GenericModule(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public SymbolTable getSymbols() {
        SymbolTable table = new SymbolTable();
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Function fann = method.getAnnotation(Function.class);
            if (fann != null) {
                String name;
                if (fann.name().equals(""))
                    name = method.getName();
                else
                    name = fann.name();
                Executable exec = new GenericExecutable(this, method, fann
                        .evaluateArgs(), evaluator);
                table.put(new Symbol(name), exec);
                System.out.println("+ " + name);
            }
        }
        return table;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Function {
        public String name() default "";
        public boolean evaluateArgs() default true;
    }

    public static class GenericExecutable implements Executable {
        private final GenericModule object;
        private final Method method;
        private final Evaluator evaluator;
        private final boolean evaluateArgs;

        public GenericExecutable(GenericModule object, Method method,
                boolean evaluateArgs, Evaluator evaluator) {
            this.object = object;
            this.method = method;
            this.evaluateArgs = evaluateArgs;
            this.evaluator = evaluator;
        }

        @Override
        public Object exec(CodeList args) throws EvaluationException {
            List<Object> evArgs = new ArrayList<Object>(args.size());
            for (Object arg : args) {
                if (evaluateArgs)
                    evArgs.add(evaluator.eval(arg));
                else
                    evArgs.add(arg);
            }
            try {
                if (method.isVarArgs())
                    packVarArgs(evArgs);
                return method.invoke(object, evArgs.toArray());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        private void packVarArgs(List<Object> evArgs) {
            Class<?>[] params = method.getParameterTypes();
            int last = params.length - 1; // index of varargs parameter
            Class<?> type = params[last].getComponentType(); // it's type

            Object[] varArgs = evArgs.subList(last, evArgs.size()).toArray();

            // New array for varargs, properly typed
            Object[] varArgsT = (Object[]) Array.newInstance(type,
                    varArgs.length);
            System.arraycopy(varArgs, 0, varArgsT, 0, varArgs.length);

            // Pack varargs
            evArgs.set(last, varArgsT);
            evArgs.subList(last + 1, evArgs.size()).clear();
        }
    }
}
