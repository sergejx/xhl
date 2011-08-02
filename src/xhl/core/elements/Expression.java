package xhl.core.elements;

/**
 * @author Sergej Chodarev
 */
public abstract class Expression extends Statement {
    protected Expression() {
    }

    protected Expression(CodePosition position) {
        super(position);
    }
}
