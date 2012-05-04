package xhl.core;

import xhl.core.elements.Position;

public class EvaluationException extends RuntimeException {
    protected final Position position;

    public EvaluationException(String message) {
        this(null, message);
    }

    public EvaluationException(Throwable cause) {
        this(null, cause);
    }

    public EvaluationException(Position position, Throwable cause) {
        this(position,
          cause.getMessage() != null ? cause.getMessage() : cause.toString());
    }

    public EvaluationException(Position position, String message) {
        super(message);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
