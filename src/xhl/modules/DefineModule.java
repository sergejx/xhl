package xhl.modules;

import xhl.core.GenericModule;
import xhl.core.elements.CodeElement;
import xhl.core.elements.Symbol;
import xhl.core.exceptions.EvaluationException;

public class DefineModule extends GenericModule {

    @Function(evaluateArgs = false)
    public void define(Symbol symbol, CodeElement valueExpr)
            throws EvaluationException {
        Object value = evaluator.eval(valueExpr);
        evaluator.putSymbol(symbol, value);
    }
}
