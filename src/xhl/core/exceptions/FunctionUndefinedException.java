package xhl.core.exceptions;

import xhl.core.elements.Expression;

public class FunctionUndefinedException extends EvaluationException {
    private final Expression expression;

    public FunctionUndefinedException(Expression exp) {
        super(exp.getPosition());
        this.expression = exp;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String getMessage() {
        return String.format("Expression '%s' is not callable.", expression);
    }
}
