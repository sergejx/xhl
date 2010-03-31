package xhl.core.exceptions;

import xhl.core.Symbol;

public class FunctionUndefinedException extends EvaluationException {
    private final Symbol symbol;

    public FunctionUndefinedException(Symbol symbol) {
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString() + symbol;
    }
}
