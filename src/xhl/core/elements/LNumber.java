package xhl.core.elements;

public class LNumber extends Expression implements ValueElement {

    private final double value;

    public LNumber(double value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
