package xhl.core.exceptions;

import xhl.core.Symbol;

public class SymbolNotDefinedException extends EvaluationException {

    private final Symbol symbol;

    public SymbolNotDefinedException(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString() + symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
