package xhl.core.elements;

public class LBoolean extends Expression {
    private final boolean value;

    public LBoolean(boolean value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
