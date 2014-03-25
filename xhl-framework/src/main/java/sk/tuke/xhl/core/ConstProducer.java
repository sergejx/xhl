package sk.tuke.xhl.core;

public class ConstProducer<T> implements Producer<T> {
    private final T value;

    public ConstProducer(T value) {
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