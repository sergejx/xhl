package xhl.core;

import java.lang.annotation.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xhl.core.elements.LList;
import xhl.core.elements.Symbol;
import xhl.core.exceptions.EvaluationException;

/**
 * Base class for implementing new modules.
 *
 * Allows to implement module functions as Java methods marked with annotation
 * <code>&#64;Function</code>.
 *
 * @author Sergej Chodarev
 */
public abstract class GenericModule implements Module {

    protected Evaluator evaluator;
    private final SymbolTable table = new SymbolTable();

    public GenericModule() {
        findFunctions();
    }

    @Override
    public SymbolTable getSymbols() {
        return table;
    }

    @Override
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * Export symbol from module
     *
     * @param symbol
     * @param value
     */
    protected void addSymbol(Symbol symbol, Object value) {
        table.put(symbol, value);
    }

    private void findFunctions() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            tryAddFunction(table, method);
        }
    }

    /**
     * If method has <code>&#64;Function</code> annotation, add it to symbols
     * table.
     *
     * @param table
     * @param method
     */
    private void tryAddFunction(SymbolTable table, Method method) {
        Function fann = method.getAnnotation(Function.class);
        if (fann != null) {
            // Find function name
            String name;
            if (fann.name().equals(""))
                name = method.getName();
            else
                name = fann.name();

            Executable exec = new GenericExecutable(method, fann.evaluateArgs());
            table.put(new Symbol(name), exec);
        }
    }

    /**
     * Indicates that method should be exported as module function.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface Function {
        /** Function name. If not specified, method name is uses. */
        public String name() default "";

        public boolean evaluateArgs() default true;
    }

    private class GenericExecutable implements Executable {
        private final Method method;
        private final boolean evaluateArgs;

        public GenericExecutable(Method method, boolean evaluateArgs) {
            this.method = method;
            this.evaluateArgs = evaluateArgs;
        }

        @Override
        public Object exec(LList args) throws EvaluationException {
            // Prepare arguments
            List<Object> evArgs = new ArrayList<Object>(args.size());
            for (Object arg : args) {
                if (evaluateArgs)
                    evArgs.add(evaluator.eval(arg));
                else
                    evArgs.add(arg);
            }
            if (method.isVarArgs())
                packVarArgs(evArgs);

            try {
                return method.invoke(GenericModule.this, evArgs.toArray());
            } catch (InvocationTargetException e) {
                throw new EvaluationException(e.getCause());
            } catch (Exception e) {
                throw new EvaluationException();
            }
        }

        /**
         * Pack arguments to array for method with variable number of arguments.
         *
         * @param evArgs
         */
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
