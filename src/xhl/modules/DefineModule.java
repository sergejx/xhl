package xhl.modules;

import xhl.core.GenericModule;
import xhl.core.elements.Expression;
import xhl.core.elements.Symbol;

public class DefineModule extends GenericModule {

    @Function(evaluateArgs = false)
    public void define(Symbol symbol, Expression valueExpr) throws Exception {
        Object value = evaluator.eval(valueExpr);
        if (evaluator.hasSymbol(symbol))
            throw new Exception(String.format("Symbol '%s' is already defined",
                    symbol));
        evaluator.putSymbol(symbol, value);
    }
}
