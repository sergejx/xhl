package xhl.core;

import java.util.List;

import xhl.core.exceptions.EvaluationException;

public interface Executable {

    public Object exec(List<?> args) throws EvaluationException;

}
