package xhl.core.elements;

public class LNumber extends CodeElement implements ValueElement {

    private final double value;

    public LNumber(double value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
