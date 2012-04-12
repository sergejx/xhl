package xhl.core;

public interface Module {
    public SymbolTable<Object> getSymbols();
    public void setEvaluator(Evaluator evaluator);
}
