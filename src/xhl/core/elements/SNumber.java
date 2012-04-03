package xhl.core.elements;

public class SNumber extends Expression {

    private final double value;

    public SNumber(double value, Position position) {
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
