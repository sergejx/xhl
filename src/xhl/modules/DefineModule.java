package xhl.modules;

import xhl.core.EvaluationException;
import xhl.core.GenericModule;
import xhl.core.elements.Symbol;

public class DefineModule extends GenericModule {

    @Element
    public void define(@Symbolic Symbol symbol, Object value)
            throws EvaluationException {
        if (evaluator.hasSymbol(symbol))
            throw new EvaluationException(String.format(
                    "Symbol '%s' is already defined", symbol));
        evaluator.putSymbol(symbol, value);
    }
}
