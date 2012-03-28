package xhl.core.elements;

public class LString extends Expression {
    private final String value;

    public LString(String value, Position position) {
        super(position);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
