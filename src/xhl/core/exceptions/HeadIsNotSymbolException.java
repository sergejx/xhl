package xhl.core.exceptions;

import xhl.core.elements.Combination;

public class HeadIsNotSymbolException extends EvaluationException {
    private final Combination list;

    public HeadIsNotSymbolException(Combination list) {
        super(list.getPosition());
        this.list = list;
    }

    public Combination getList() {
        return list;
    }

    @Override
    public String toString() {
        return "Head of the list is not executable.";
    }
}
