package xhl.modules;

import xhl.core.GenericModule;
import xhl.core.elements.Symbol;

public class DefineModule extends GenericModule {

    @Function
    public void define(@Symbolic Symbol symbol, Object value) throws Exception {
        if (evaluator.hasSymbol(symbol))
            throw new Exception(String.format("Symbol '%s' is already defined",
                    symbol));
        evaluator.putSymbol(symbol, value);
    }
}
