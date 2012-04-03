package xhl.examples.entity;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final String name;
    private final List<Attribute> attributes = new ArrayList<Attribute>();

    public Entity(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean add(Attribute e) {
        return attributes.add(e);
    }
}
