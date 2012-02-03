package xhl.core;

import xhl.core.elements.LList;
import xhl.core.exceptions.EvaluationException;

public interface Executable {

    public Object exec(LList args) throws EvaluationException;

}
