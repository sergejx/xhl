package xhl.examples.entity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import xhl.core.Builder;

import static com.google.common.collect.Lists.newArrayList;

public class Entity {
    private final String name;
    private final List<Attribute> attributes = newArrayList();
    private final List<Builder<Boolean>> validation = newArrayList();

    public Entity(String name) {
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

    public boolean addValidation(Builder<Boolean> rule) {
        return validation.add(rule);
    }

    public void generate() {
        try {
            Velocity.init();
            VelocityContext context = new VelocityContext();
            context.put("name", name);
            context.put("attributes", attributes);
            context.put("validation", validation);
            context.put("Util", new Util());
            Template template = Velocity.getTemplate("src/xhl/examples/entity/entity.vm");
            FileWriter wr = new FileWriter(name + ".java");
            template.merge(context, wr);
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
