package xhl.core.exceptions;

import xhl.core.elements.Symbol;

public class SymbolNotDefinedException extends EvaluationException {

    private final Symbol symbol;

    public SymbolNotDefinedException(Symbol symbol) {
        super(symbol.getPosition());
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String getMessage() {
        return String.format("Symbol '%s' was not defined.", symbol);
    }
}
