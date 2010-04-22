package xhl.core;

import xhl.core.elements.DataList;
import xhl.core.exceptions.EvaluationException;

public interface Executable {

    public Object exec(DataList args) throws EvaluationException;

}
