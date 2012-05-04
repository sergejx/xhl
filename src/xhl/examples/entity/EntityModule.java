package xhl.examples.entity;

import java.util.Map;

import xhl.core.GenericModule;
import xhl.core.elements.Block;
import xhl.core.elements.Symbol;
import xhl.core.validator.Validator;
import xhl.examples.entity.Type.T;
import static xhl.examples.entity.Type.*;

public class EntityModule extends GenericModule {

    private Module module;
    private Entity currentEntity;

    public Module getModule() {
        return module;
    }

    @Function(evaluateArgs = false)
    public void module(Symbol name, Block body) {
        module = new Module(name.getName());

        // Initialize all defined entities
        Map<Symbol, xhl.core.validator.Type> defined =
                Validator.backwardDefunitions(body, getSchema());
        for (Symbol sym : defined.keySet()) {
            if (defined.get(sym).isNamed("Entity")) {
                Entity entity = new Entity(sym.getName());
                module.add(entity);
                evaluator.putSymbol(sym, entity);
            }
        }

        evaluator.eval(body);
    }

    @Function(evaluateArgs = false)
    public Entity entity(Symbol name, Block attrs) {
        currentEntity = (Entity) evaluator.getSymbol(name);
        evaluator.eval(attrs);
        return currentEntity;
    }

    @Function(name = ":")
    public void attribute(@Symbolic Symbol name, @Symbolic Symbol typeName) {
        Type type;
        if (typeName.isNamed("int"))
            type = simple(T.INT);
        else if (typeName.isNamed("string"))
            type = simple(T.STRING);
        else if (typeName.isNamed("boolean"))
            type = simple(T.BOOLEAN);
        else {
            Entity ref = module.get(typeName.getName());
            type = reference(ref);
        }
        currentEntity.add(new Attribute(name.getName(), type));
    }
}
