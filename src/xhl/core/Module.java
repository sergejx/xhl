package xhl.core;

import xhl.core.validator.Schema;

public interface Module {
    public Environment<Object> getSymbols();
    public Schema getSchema();
    public void setEvaluator(Evaluator evaluator);
}
