package xhl.core;

import java.util.List;

import xhl.core.exceptions.EvaluationException;
import xhl.core.exceptions.FunctionUndefinedException;
import xhl.core.exceptions.HeadIsNotSymbolException;
import xhl.core.exceptions.SymbolNotDefinedException;

public class Evaluator {
    private final SymbolTable symbolTable = new SymbolTable();

    public void loadModule(Module module) {
        symbolTable.putAll(module.getSymbols());
    }

    public Object eval(Object obj) throws EvaluationException {
        if (obj instanceof Symbol) {
            Symbol sym = (Symbol) obj;
            if (symbolTable.containsKey(sym))
                return symbolTable.get(sym);
            else
                throw new SymbolNotDefinedException(sym);
        } else if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            Object head = list.get(0);
            if (!(head instanceof Symbol))
                throw new HeadIsNotSymbolException();
            Object func = eval(head);
            if (!(func instanceof Executable))
                throw new FunctionUndefinedException((Symbol) head);
            return ((Executable) func).exec(list.subList(1, list.size()));
        } else {
            return obj;
        }
    }

    public Object evalAll(List<Object> exprs) throws EvaluationException {
        Object result = null;
        for (Object expr : exprs) {
            result = eval(expr);
        }
        return result;
    }

    public void putSymbol(Symbol sym, Object value) {
        symbolTable.put(sym, value);
    }

    public Object getSymbol(Symbol sym) {
        return symbolTable.get(sym);
    }
}
