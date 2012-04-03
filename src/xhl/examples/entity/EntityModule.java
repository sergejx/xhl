package xhl.examples.entity;

import static xhl.examples.entity.Type.reference;
import static xhl.examples.entity.Type.simple;

import java.util.LinkedList;
import java.util.List;

import xhl.core.EvaluationException;
import xhl.core.GenericModule;
import xhl.core.elements.*;
import xhl.examples.entity.Type.T;

public class EntityModule extends GenericModule {

    private Module module;

    public Module getModule() {
        return module;
    }

    private class EntityBuilder {
        private final String name;
        private final LMap attrs;
        private Entity entity;

        public EntityBuilder(String name, LMap attrs) {
            this.name = name;
            this.attrs = attrs;
        }

        public Entity createEntity() {
            entity = new Entity(name);
            return entity;
        }
        public void fillAttributes() {
            for (Expression key : attrs.keySet()) {
                try {
                    Symbol atrName = (Symbol) key;
                    Symbol atrType = (Symbol) attrs.get(key);
                    Type type;
                    if (atrType.isNamed("int"))
                        type = simple(T.INT);
                    else if (atrType.isNamed("string"))
                        type = simple(T.STRING);
                    else if (atrType.isNamed("boolean"))
                        type = simple(T.BOOLEAN);
                    else {
                        Entity ref = module.get(atrType.getName());
                        type = reference(ref);
                    }
                    entity.add(new Attribute(atrName.getName(), type));
                } catch (ClassCastException e) {
                    throw new EvaluationException(key.getPosition(),
                            "Unexpected expression inside attribute definition");
                }
            }
        }
    }

    @Function(evaluateArgs = false)
    public void module(Symbol name, Block body) {
        module = new Module(name.getName());
        List<EntityBuilder> builders = new LinkedList<EntityBuilder>();
        for (Expression exp : body) {
            try {
                EntityBuilder b = (EntityBuilder) evaluator.eval(exp);
                module.add(b.createEntity());
                builders.add(b);
            } catch (ClassCastException e) {
                throw new EvaluationException(exp.getPosition(),
                        "Unexpected expression inside module");
            }
        }
        for (EntityBuilder b : builders)
            b.fillAttributes();
    }

    @Function(evaluateArgs = false)
    public EntityBuilder entity(Symbol name, LMap attrs) {
        return new EntityBuilder(name.getName(), attrs);
    }
}
