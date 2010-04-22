package xhl.core.exceptions;

import xhl.core.elements.Symbol;

public class FunctionUndefinedException extends EvaluationException {
    private final Symbol symbol;

    public FunctionUndefinedException(Symbol symbol) {
        super(symbol.getPosition());
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return String.format("Symbol '%s' is not callable.", symbol);
    }
}
