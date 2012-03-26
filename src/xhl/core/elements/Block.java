package xhl.core.elements;

import java.util.Iterator;
import java.util.List;

/**
 * @author Sergej Chodarev
 */
public class Block extends Expression implements Iterable<Statement> {
    private final List<Statement> body;

    public Block(List<Statement> body, CodePosition position) {
        super(position);
        this.body = body;
    }

    public List<Statement> getStatements() {
        return body;
    }

    @Override
    public Iterator<Statement> iterator() {
        return body.iterator();
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
