package xhl.core;

import xhl.core.elements.CodeElement.CodePosition;

public class EvaluationException extends RuntimeException {
    protected final CodePosition position;

    public EvaluationException(Throwable cause) {
        this(null, cause);
    }

    public EvaluationException(CodePosition position, Throwable cause) {
        this(position,
          cause.getMessage() != null ? cause.getMessage() : cause.toString());
    }

    public EvaluationException(CodePosition position, String message) {
        super(message);
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
