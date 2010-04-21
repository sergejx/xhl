package xhl.modules;

import xhl.core.GenericModule;
import xhl.core.Symbol;
import xhl.core.exceptions.EvaluationException;

public class DefineModule extends GenericModule {

    @Function(evaluateArgs=false)
    public void define(Symbol symbol, Object value) throws EvaluationException {
        value = evaluator.eval(value);
        evaluator.putSymbol(symbol, value);
        System.out.println(symbol + ": " + value);
    }
}
