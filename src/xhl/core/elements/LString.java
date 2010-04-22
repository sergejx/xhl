package xhl.core.elements;

public class LString extends CodeElement implements ValueElement {
    private final String value;

    public LString(String value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
