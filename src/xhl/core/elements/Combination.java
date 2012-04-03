package xhl.core.elements;

/**
 * Combination may be function application (in form of sequence of expressions)
 * or infix operator application.
 *
 * It is similar to SList, but has different interpretation during evaluation.
 *
 * @author Sergej Chodarev
 */
public class Combination extends ExpressionsList {
    public Combination(Position position) {
        super(position);
    }

    /** Get first item of the combination -- usually function name */
    public Expression head() {
        return list.get(0);
    }

    /** Get all but first items -- function arguments */
    public SList tail() {
        return new SList(list.subList(1, list.size()));
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
