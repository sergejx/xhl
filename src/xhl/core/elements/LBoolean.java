package xhl.core.elements;

public class LBoolean extends CodeElement implements ValueElement {
    private final boolean value;

    public LBoolean(boolean value, CodePosition position) {
        super(position);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
