package xhl.core;

import java.lang.annotation.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xhl.core.elements.SList;
import xhl.core.elements.Symbol;

import com.google.common.base.Optional;

import static java.util.Arrays.asList;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;

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

    /**
     * Export self-referencing symbol
     *
     * @param symbol
     */
    protected void addKeyword(Symbol symbol) {
        table.put(symbol, symbol);
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

            // Check which parameters to evaluate
            Annotation[][] pann = method.getParameterAnnotations();
            boolean[] evaluateArgs = new boolean[pann.length];
            for (int i = 0; i < pann.length; i++) {
                Optional<Annotation> ann =
                        tryFind(asList(pann[i]), instanceOf(Symbolic.class));
                if (ann.isPresent())
                    evaluateArgs[i] = false;
                else
                    evaluateArgs[i] = fann.evaluateArgs();
            }

            Executable exec = new GenericExecutable(method, evaluateArgs);
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    protected @interface Symbolic {
    }

    private class GenericExecutable implements Executable {
        private final Method method;
        private final boolean[] evaluateArgs;

        public GenericExecutable(Method method, boolean[] evaluateArgs) {
            this.method = method;
            this.evaluateArgs = evaluateArgs;
        }

        @Override
        public Object exec(SList args) throws EvaluationException {
            // Prepare arguments
            List<Object> evArgs = new ArrayList<Object>(args.size());
            for (int i = 0; i < args.size(); i++) {
                if (i >= evaluateArgs.length // for varargs
                        || evaluateArgs[i])
                    evArgs.add(evaluator.eval(args.get(i)));
                else
                    evArgs.add(args.get(i));
            }
            if (method.isVarArgs())
                packVarArgs(evArgs);

            try {
                return method.invoke(GenericModule.this, evArgs.toArray());
            } catch (InvocationTargetException e) {
                throw new EvaluationException(e.getCause());
            } catch (Exception e) {
                throw new EvaluationException(e);
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
            Object[] varArgsT =
                    (Object[]) Array.newInstance(type, varArgs.length);
            System.arraycopy(varArgs, 0, varArgsT, 0, varArgs.length);

            // Pack varargs
            evArgs.set(last, varArgsT);
            evArgs.subList(last + 1, evArgs.size()).clear();
        }
    }
}
