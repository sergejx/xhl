package xhl.core.elements;

import java.util.Iterator;
import java.util.List;

/**
 * @author Sergej Chodarev
 */
public class Block extends Expression implements Iterable<Expression> {
    private final List<Expression> body;

    public Block(List<Expression> body, CodePosition position) {
        super(position);
        this.body = body;
    }

    public List<Expression> getExpressions() {
        return body;
    }

    @Override
    public Iterator<Expression> iterator() {
        return body.iterator();
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
