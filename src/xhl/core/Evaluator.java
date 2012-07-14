package xhl.core;

import java.util.*;

import xhl.core.elements.*;

/**
 * XHL code evaluator.
 *
 * @author Sergej Chodarev
 */
public class Evaluator implements ElementVisitor<Object>{
    private final Environment<Object> environment = new Environment<>();

    public void loadModule(Module module) {
        module.setEvaluator(this);
        environment.putAll(module.getSymbols());
    }

    /**
     * Evaluate language object.
     *
     * @param obj
     *            object to evaluate
     * @return result of evaluation
     * @throws EvaluationException
     */
    public Object eval(Expression obj) throws EvaluationException {
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
    public Object evalAll(Block exprs) throws EvaluationException {
        Executable func = (Executable) environment.get(new Symbol("!script"));
        if (func != null) {
            SList args = new SList(exprs.getPosition());
            args.add(exprs);
            return func.exec(args);
        }
        Object result = null;
        for (Expression expr : exprs) {
            result = eval(expr);
        }
        return result;
    }

    /**
     * Put symbol into symbol table.
     */
    public void putSymbol(Symbol symbol, Object value) {
        environment.put(symbol, value);
    }

    /**
     * Get value of symbol from symbol table.
     *
     * @return value associated with a symbol
     */
    public Object getSymbol(Symbol symbol) {
        return environment.get(symbol);
    }

    /**
     * Does symbol table contain specified symbol?
     *
     * @return <code>true</code> if symbol is defined in symbol table
     */
    public boolean hasSymbol(Symbol symbol) {
        return environment.containsKey(symbol);
    }

    public void pushEnvironment() {
        environment.push();
    }

    public void popEnvironment() {
        environment.pop();
    }

    public Object putGlobalSymbol(Symbol sym, Object value) {
        return environment.putGlobal(sym, value);
    }

    @Override
    public Object visit(SNumber num) {
        return num.getValue();
    }

    @Override
    public Object visit(SBoolean bool) {
        return bool.getValue();
    }

    @Override
    public Object visit(SString str) {
        return str.getValue();
    }

    @Override
    public Object visit(SList lst) {
        List<Object> list = new LinkedList<>();
        for (Expression item: lst) {
            Object value = item.accept(this);
            list.add(value);
        }
        return list;
    }

    @Override
    public Object visit(SMap map) {
        Map<Object, Object> vmap = new HashMap<>();
        for (Expression key: map.keySet()) {
            Object vkey = key.accept(this);
            Object vvalue = map.get(key).accept(this);
            vmap.put(vkey, vvalue);
        }
        return vmap;
    }

    @Override
    public Object visit(Symbol sym) {
        if (environment.containsKey(sym))
            return environment.get(sym);
        else
            throw new EvaluationException(sym.getPosition(),
                    String.format("Symbol '%s' was not defined.", sym));
    }

    @Override
    public Object visit(Combination cmb) {
        Expression head = cmb.head();
        Object func = head.accept(this);
        if (!(func instanceof Executable))
            throw new EvaluationException(head.getPosition(),
                    String.format("Expression '%s' is not callable.", head));
        try {
            return ((Executable) func).exec(cmb.tail());
        } catch (EvaluationException e) {
            // Add error position if not present
            if (e.getPosition() == null)
                throw new EvaluationException(cmb.getPosition(), e);
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
