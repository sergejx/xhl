package xhl.core.exceptions;

import xhl.core.CodeList;

public class HeadIsNotSymbolException extends EvaluationException {
    private final CodeList list;

    public HeadIsNotSymbolException(CodeList list) {
        super(list.getPosition());
        this.list = list;
    }

    public CodeList getList() {
        return list;
    }

    @Override
    public String toString() {
        return "Head of the list is not executable.";
    }
}
