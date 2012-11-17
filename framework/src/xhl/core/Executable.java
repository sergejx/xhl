package xhl.core;

import xhl.core.elements.SList;

public interface Executable {

    public Object exec(SList args) throws EvaluationException;

}
