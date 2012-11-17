package sk.tuke.xhl.core.elements;

/**
 * Expression is a base class for XHL code elements
 *
 * @author Sergej Chodarev
 */
public abstract class Expression {
    private final Position position;

    protected Expression() {
        this(null);
    }

    protected Expression(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * Accept a visitor object and call corresponding visit method of it.
     *
     * @return Result from the visitor.
     */
    public abstract <R> R accept(ElementVisitor<R> visitor);

}
