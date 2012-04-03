package xhl.examples.entity;

public class Type {
    public enum T {INT, STRING, BOOLEAN, REFERENCE};

    private final T type;
    private Entity entity;

    private Type(T type) {
        this.type = type;
    }

    private Type(Entity entity) {
        this.type = T.REFERENCE;
        this.entity = entity;
    }

    public static Type simple(T type) {
        return new Type(type);
    }

    public static Type reference(Entity entity) {
        return new Type(entity);
    }

    public T getType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }
}
