/* XHL - Extensible Host Language
 * Copyright 2012 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.tuke.xhl.core;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import sk.tuke.xhl.core.elements.Block;
import sk.tuke.xhl.core.elements.SList;
import sk.tuke.xhl.core.elements.Symbol;
import sk.tuke.xhl.core.validator.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;
import static sk.tuke.xhl.core.ModulesProvider.ModulesLoader;
import static sk.tuke.xhl.core.validator.ElementSchema.ParamSpec.val;
import static sk.tuke.xhl.core.validator.ElementSchema.ParamSpec.variadic;


/**
 * Base class for implementing new modules.
 *
 * Allows to implement module functions as Java methods marked with annotation
 * <code>&#64;Element</code>.
 *
 * @author Sergej Chodarev
 */
public abstract class GenericModule implements Module {

    protected Evaluator evaluator;
    private final Environment<Object> table = new Environment<>();
    private final Map<Symbol, ElementValidator> validators = new HashMap<>();
    private final Map<String, Executable> localElements = new HashMap<>();
    private Schema schema;
    /** Imported modules */
    private final List<Module> modules = new ArrayList<>();

    public GenericModule() {
        findEvalFunctions();
        findCheckFunctions();
        if (canHaveModules())
            loadModules();
    }

    @Override
    public boolean isLanguage() {
        return false; // By default modules are not languages
    }

    @Override
    public Environment<Object> getSymbols() {
        return table;
    }

    @Override
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
        // Set evaluators for imported modules
        for (Module module : modules) {
            module.setEvaluator(evaluator);
        }
    }

    /**
     * Return module schema.
     */
    @Override
    public Schema getSchema() {
        if (schema == null)  // Read schema when it is needed
            schema = readSchema();
        return schema;
    }

    protected InputStream findSchemaStream() {
        Class<? extends GenericModule> clazz = this.getClass();
        return clazz.getResourceAsStream(schemaFileName());
    }

    private String schemaFileName() {
        return this.getClass().getSimpleName() + ".schema";
    }

    /**
     * Is it needed to load imported modules?
     * This method can be used to suppress loading of language schema -- if
     * program is evaluated without validation and can not have modules,
     * its schema would not be read.
     *
     * @return <code>false</code> if imported modules should not be loaded.
     */
    protected boolean canHaveModules() {
        return true;
    }

    /**
     * Read or create module schema.
     * Try to read module schema from a file with the same name as the class but
     * with extension ".schema". If it is not available, create generic schema,
     * that contains all elements defined in the module with the most generic
     * properties allowing to pass the validation.
     */
    private Schema readSchema() {
        Schema sch;
        InputStream in = findSchemaStream();
        if (in != null) {
            ValidatorLanguage lang = new ValidatorLanguage();
            LanguageProcessor schemaProcessor = new LanguageProcessor(lang);
            Block program;
            try {
                program = Reader.read(new InputStreamReader(in),
                        schemaFileName()).get();
                schemaProcessor.executeWithoutValidation(program);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sch = lang.getReadSchema();
        } else {
            sch = makeGenericSchema();
        }
        addCustomValidators(sch);
        return sch;
    }

    /**
     * Make a generic schema based in evaluation functions.
     */
    private Schema makeGenericSchema() {
        Schema schema = new Schema();
        for (Symbol symbol : table.keySet()) {
            ElementSchema elem = new ElementSchema(symbol);
            elem.setType(Type.AnyType);
            elem.setParams(ImmutableList.of(variadic(val(Type.AnyType))));
            schema.put(elem);
        }
        return schema;
    }

    private void addCustomValidators(Schema schema) {
        for (ElementSchema element : schema) {
            if (validators.containsKey(element.getSymbol()))
                element.setValidator(validators.get(element.getSymbol()));
        }
    }

    /**
     * Export symbol from module
     */
    protected void addSymbol(Symbol symbol, Object value) {
        table.put(symbol, value);
    }

    /**
     * Export self-referencing symbol
     */
    protected void addKeyword(Symbol symbol) {
        table.put(symbol, symbol);
    }

    /**
     * Get an element that is not defined globally.
     *
     * @param name Element name
     * @return Element evaluation function.
     */
    protected Executable getLocalElement(String name) {
        return localElements.get(name);
    }

    private void findEvalFunctions() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            tryAddFunction(table, method);
        }
    }

    private void findCheckFunctions() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            tryAddCheckFunction(method);
        }
    }

    /**
     * If method has <code>&#64;Element</code> annotation, add it to symbols
     * table.
     */
    private void tryAddFunction(Environment<Object> table, Method method) {
        Element annotation = method.getAnnotation(Element.class);
        if (annotation != null) {
            // Find function name
            String name;
            if (annotation.name().equals(""))
                name = method.getName();
            else
                name = annotation.name();

            // Check which parameters to evaluate
            Annotation[][] pann = method.getParameterAnnotations();
            boolean[] evaluateArgs = new boolean[pann.length];
            for (int i = 0; i < pann.length; i++) {
                Optional<Annotation> ann =
                        tryFind(asList(pann[i]), instanceOf(Symbolic.class));
                evaluateArgs[i] = !ann.isPresent();
            }

            Executable exec = new GenericExecutable(method, evaluateArgs);
            if (annotation.local())
                localElements.put(name, exec);
            else
                table.put(new Symbol(name), exec);
        }
    }

    /**
     * If a method has the <code>&#64;Check</code> annotation,
     * register it as a check function for an element.
     */
    private void tryAddCheckFunction(Method method) {
        Check annotation = method.getAnnotation(Check.class);
        if (annotation != null) {
            String name = annotation.name();
            ElementValidator validator = new CustomValidator(method);
            validators.put(new Symbol(name), validator);
        }
    }

    private void loadModules() {
        for (Schema.Import imp: getSchema().getImports()) {
            ModulesLoader loader = new ModulesLoader();
            Module mod = loader.loadModule(imp.getModule());
            modules.add(mod);
            if (imp.allElements()) {
                table.putAll(mod.getSymbols());
            } else {
                for (Symbol element : imp) {
                    table.put(element, mod.getSymbols().get(element));
                }
            }
        }
    }

    /**
     * Indicates that a method is an evaluation function for a module element.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface Element {
        /** Element name. If not specified, method name is uses. */
        public String name() default "";

        /**
         * Local element is not registered in global module namespace.
         * Instead it should be bound by corresponding block evaluation
         * function.
         */
        public boolean local() default false;
    }

    /**
     * Mark for an element check function.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface Check {
        /** The name of checked method. */
        public String name();
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
            List<Object> evArgs = new ArrayList<>(args.size());
            for (int i = 0; i < args.size(); i++) {
                if (i >= evaluateArgs.length // for varargs
                        || evaluateArgs[i])
                    evArgs.add(evaluator.eval(args.get(i)));
                else
                    evArgs.add(args.get(i));
            }
            processBuilders(evArgs);

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

        private void processBuilders(List<Object> evArgs) {
            Class<?>[] params = method.getParameterTypes();
            for (int i = 0; i < evArgs.size(); i++) {
                Object arg = evArgs.get(i);
                Class<?> param;
                if (i < params.length)
                    param = params[i];
                else // varargs
                    param = params[params.length - 1];
                boolean pb = Producer.class.isAssignableFrom(param);
                boolean ab = Producer.class.isAssignableFrom(arg.getClass());
                if (!pb && ab) {
                    evArgs.set(i, ((Producer<?>) arg).toValue());
                } else if (pb && !ab) {
                    ConstProducer<?> b = new ConstProducer<>(arg);
                    evArgs.set(i, b);
                }
            }
        }

        /**
         * Pack arguments to array for method with variable number of arguments.
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


    private class CustomValidator implements ElementValidator {

        private final Method method;

        public CustomValidator(Method method) {
            this.method = method;
        }

        @Override
        public Map<Symbol, Type> forwardDefinitions(SList args) {
            return new HashMap<>();
        }

        @Override
        public ValidationResult check(Validator validator, SList tail) {
            Object[] args = new Object[tail.size() + 1];
            args[0] = validator;
            for (int i = 0; i < tail.size(); i++) {
                args[i+1] = tail.get(i);
            }
            try {
                return (ValidationResult) method.invoke(GenericModule.this,
                        args);
            } catch (InvocationTargetException e) {
                throw new EvaluationException(e.getCause());
            } catch (Exception e) {
                throw new EvaluationException(e);
            }
        }
    }
}
