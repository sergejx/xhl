package xhl.core.exceptions;

import xhl.core.CodeElement.CodePosition;

public class EvaluationException extends Exception {
    protected final CodePosition position;

    public EvaluationException() {
        this(null);
    }

    public EvaluationException(CodePosition position) {
        super();
        this.position = position;
    }

    public CodePosition getPosition() {
        return position;
    }
}
