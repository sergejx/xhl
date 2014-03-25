package sk.tuke.xhl.core;

public interface Producer<T> {
    public T toValue();
    public String toCode();
}
