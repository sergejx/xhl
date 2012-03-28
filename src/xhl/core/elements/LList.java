package xhl.core.elements;

import java.util.List;

/**
 * List
 *
 * @author Sergej Chodarev
 */
public class LList extends ExpressionsList implements Iterable<Expression> {

    public LList(Position position) {
        super(position);
    }

    LList(List<Expression> l) {
        list.addAll(l);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
