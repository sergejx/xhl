package sk.tuke.xhl.examples.entity;

public class Attribute {
    private final String name;
    private final Type type;

    public Attribute(String name, Type type) {
        super();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
