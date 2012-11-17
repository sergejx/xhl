package sk.tuke.xhl.core.elements;

import java.util.List;

/**
 * List
 *
 * @author Sergej Chodarev
 */
public class SList extends ExpressionsList implements Iterable<Expression> {

    public SList(Position position) {
        super(position);
    }

    SList(List<Expression> l) {
        list.addAll(l);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
