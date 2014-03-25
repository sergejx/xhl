package sk.tuke.xhl.core.elements;

public class SString extends Expression {
    private final String value;

    public SString(String value, Position position) {
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
