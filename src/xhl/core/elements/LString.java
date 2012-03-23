package xhl.core.elements;

public class LString extends Expression implements ValueElement {
    private final String value;

    public LString(String value, CodePosition position) {
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
