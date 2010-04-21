package xhl.core;

import xhl.core.exceptions.*;

/**
 * XHL code evaluator.
 *
 * @author Sergej Chodarev
 */
public class Evaluator {
    private final SymbolTable symbolTable = new SymbolTable();

    public void loadModule(Module module) {
        symbolTable.putAll(module.getSymbols());
    }

    /**
     * Evaluate language object.
     *
     * @param obj
     *            object to evaluate
     * @return result of evaluation
     * @throws EvaluationException
     */
    public Object eval(Object obj) throws EvaluationException {
        if (obj instanceof Symbol) {
            Symbol sym = (Symbol) obj;
            if (symbolTable.containsKey(sym))
                return symbolTable.get(sym);
            else
                throw new SymbolNotDefinedException(sym);
        } else if (obj instanceof CodeList) {
            return evalList((CodeList) obj);
        } else {
            return obj;
        }
    }

    private Object evalList(CodeList list) throws EvaluationException {
        Object head = list.head();
        if (!(head instanceof Symbol))
            throw new HeadIsNotSymbolException(list);
        Object func = eval(head);
        if (!(func instanceof Executable))
            throw new FunctionUndefinedException((Symbol) head);
        return ((Executable) func).exec(list.tail());
    }

    /**
     * Evaluate list of program expressions
     *
     * @param exprs
     *            list containing program expressions
     * @return result of evaluation
     * @throws EvaluationException
     */
    public Object evalAll(CodeList exprs) throws EvaluationException {
        Object result = null;
        for (Object expr : exprs) {
            result = eval(expr);
        }
        return result;
    }

    /**
     * Put symbol into symbol table.
     *
     * @param symbol
     * @param value
     */
    public void putSymbol(Symbol symbol, Object value) {
        symbolTable.put(symbol, value);
    }

    /**
     * Get value of symbol from symbol table.
     *
     * @param symbol
     * @return value associated with symbol
     */
    public Object getSymbol(Symbol symbol) {
        return symbolTable.get(symbol);
    }
}
