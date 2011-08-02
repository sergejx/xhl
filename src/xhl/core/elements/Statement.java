package xhl.core.elements;

/**
 * @author Sergej Chodarev
 */
public abstract class Statement extends CodeElement {
    protected Statement() {
    }

    protected Statement(CodePosition position) {
        super(position);
    }
}
