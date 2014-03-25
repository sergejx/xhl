package sk.tuke.xhl.core;

import sk.tuke.xhl.core.elements.SList;

public interface Executable {

    public Object exec(SList args) throws EvaluationException;

}
