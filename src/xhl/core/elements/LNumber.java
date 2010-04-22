package xhl.core.elements;

public class LNumber extends CodeElement {

    private final double value;

    public LNumber(double value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
