package xhl.core;

public interface Module {
    public SymbolTable getSymbols();
    public void setEvaluator(Evaluator evaluator);
}
