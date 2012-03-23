package xhl.core.elements;

/**
 * Combination may be function application (in form of sequence of expressions)
 * or infix operator application.
 *
 * Technically the same thing as LList, it just has different interpretation
 * during evaluation.
 *
 * @author Sergej Chodarev
 */
public class Combination extends LList {
    public Combination(CodePosition position) {
        super(position);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
