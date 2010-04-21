package xhl.core;

public class LString extends CodeElement {
    private final String value;

    public LString(String value, CodePosition position) {
        super(position);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
