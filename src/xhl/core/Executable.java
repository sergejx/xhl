package xhl.core;

import xhl.core.exceptions.EvaluationException;

public interface Executable {

    public Object exec(CodeList args) throws EvaluationException;

}
