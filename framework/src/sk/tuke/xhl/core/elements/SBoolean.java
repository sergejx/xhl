package sk.tuke.xhl.core.elements;

public class SBoolean extends Expression {
    private final boolean value;

    public SBoolean(boolean value, Position position) {
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
