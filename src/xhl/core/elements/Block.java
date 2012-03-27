package xhl.core.elements;

import java.util.List;

/**
 * @author Sergej Chodarev
 */
public class Block extends ExpressionsList implements Iterable<Expression> {

    public Block(List<Expression> body, CodePosition position) {
        super(position, body);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
