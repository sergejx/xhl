package sk.tuke.xhl.core.elements;


/**
 * @author Sergej Chodarev
 */
public class Block extends ExpressionsList implements Iterable<Expression> {

    public Block(Position position) {
        super(position);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
