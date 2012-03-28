package xhl.core;

import xhl.core.elements.LList;

public interface Executable {

    public Object exec(LList args) throws EvaluationException;

}
