package xhl.core.exceptions;

import xhl.core.Symbol;

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
    public String toString() {
        return String.format("Symbol '%s' was not defined.", symbol);
    }
}
