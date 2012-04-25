package xhl.core;

public class ConstBuilder<T> implements Builder<T> {
    private final T value;

    public ConstBuilder(T value) {
        this.value = value;
    }

    @Override
    public T toValue() {
        return value;
    }

    @Override
    public String toCode() {
        // TODO: Add proper conversion for basic types
        return value.toString();
    }
}