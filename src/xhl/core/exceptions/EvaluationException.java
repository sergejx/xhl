package xhl.core.exceptions;

import xhl.core.elements.CodeElement.CodePosition;

public class EvaluationException extends RuntimeException {
    protected final CodePosition position;

    protected EvaluationException(CodePosition position) {
        this(null, position);
    }

    public EvaluationException(Throwable cause) {
        this(cause, null);
    }

    public EvaluationException(Throwable cause, CodePosition position) {
        super(cause.getMessage() != null ? cause.getMessage()
                                         : cause.toString(),
                cause);
        this.position = position;
    }

    public CodePosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
