package xhl.core;

import java.util.*;

import xhl.core.elements.*;
import xhl.core.exceptions.EvaluationException;
import xhl.core.exceptions.FunctionUndefinedException;
import xhl.core.exceptions.SymbolNotDefinedException;

/**
 * XHL code evaluator.
 *
 * @author Sergej Chodarev
 */
public class Evaluator implements ElementVisitor<Object>{
    private final SymbolTable symbolTable = new SymbolTable();

    public void loadModule(Module module) {
        module.setEvaluator(this);
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
    public Object eval(CodeElement obj) throws EvaluationException {
        return (obj).accept(this);
    }

    /**
     * Evaluate list of program expressions
     *
     * @param exprs
     *            list containing program expressions
     * @return result of evaluation
     * @throws EvaluationException
     */
    public Object evalAll(List<Expression> exprs) throws EvaluationException {
        Object result = null;
        for (Expression expr : exprs) {
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

    /**
     * Does symbol table contain specified symbol?
     *
     * @param symbol
     * @return <code>true</code> if symbol is defined in symbol table
     */
    public boolean hasSymbol(Symbol symbol) {
        return symbolTable.containsKey(symbol);
    }

    @Override
    public Object visit(LNumber num) {
        return num.getValue();
    }

    @Override
    public Object visit(LBoolean bool) {
        return bool.getValue();
    }

    @Override
    public Object visit(LString str) {
        return str.getValue();
    }

    @Override
    public Object visit(LList lst) {
        List<Object> list = new LinkedList<Object>();
        for (Expression item: lst) {
            Object value = item.accept(this);
            list.add(value);
        }
        return list;
    }

    @Override
    public Object visit(LMap map) {
        Map<Object, Object> vmap = new HashMap<Object, Object>();
        for (Expression key: map.keySet()) {
            Object vkey = key.accept(this);
            Object vvalue = map.get(key).accept(this);
            vmap.put(vkey, vvalue);
        }
        return vmap;
    }

    @Override
    public Object visit(Symbol sym) {
        if (symbolTable.containsKey(sym))
            return symbolTable.get(sym);
        else
            throw new SymbolNotDefinedException(sym);
    }

    @Override
    public Object visit(Combination cmb) {
        Expression head = cmb.head();
        Object func = head.accept(this);
        if (!(func instanceof Executable))
            throw new FunctionUndefinedException(head);
        try {
            return ((Executable) func).exec(cmb.tail());
        } catch (EvaluationException e) {
            // Add error position if not present
            if (e.getPosition() == null)
                throw new EvaluationException(e, cmb.getPosition());
            else
                throw e;
        }
    }

    @Override
    public Object visit(Block blk) {
        Object result = null;
        for (Expression expr : blk) {
            result = eval(expr);
        }
        return result;
    }
}
