package sk.tuke.xhl.examples.entity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import sk.tuke.xhl.core.Producer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final String name;
    private final List<Attribute> attributes = new ArrayList<>();
    private final List<Producer<Boolean>> validation = new ArrayList<>();

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

    public boolean addValidation(Producer<Boolean> rule) {
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
            Template template = Velocity.getTemplate("entity.vm");
            FileWriter wr = new FileWriter(name + ".java");
            template.merge(context, wr);
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
