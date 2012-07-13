package xhl.core;

import xhl.core.validator.Schema;

/**
 * A language module provides language elements bound to the symbols.
 * To implement a module you should extend <code>GenericModule</code> class.
 *
 * @author Sergej Chodarev
 */
public interface Module {
    /**
     * Get symbols exported by a module with associated evaluation functions
     * or values.
     *
     * @return Environment with symbols exported by a module.
     */
    public Environment<Object> getSymbols();

    /**
     * Get module schema.
     */
    public Schema getSchema();

    /**
     * Register an evaluator with the module.
     * Module can use it to evaluate expressions and to manipulate
     * the evaluation environment. If module imports other modules,
     * it should pass the evaluator to them too.
     *
     * @param evaluator
     */
    public void setEvaluator(Evaluator evaluator);

    /**
     * Is the module a main module of a language?
     */
    public boolean isLanguage();
}
