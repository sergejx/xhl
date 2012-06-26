package xhl.core.validator;

import xhl.core.GenericModule;
import xhl.core.Language;
import xhl.core.Module;
import xhl.core.elements.Block;
import xhl.core.elements.Symbol;
import xhl.core.validator.ElementSchema.DefSpec;
import xhl.core.validator.ElementSchema.ParamSpec;

import java.util.Deque;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newLinkedList;
import static xhl.core.validator.ElementSchema.DefSpec.global;

public class ValidatorLanguage extends GenericModule implements Language {
    /**
     * A stack representing currently processed elements.
     */
    private Deque<ElementSchema> currentElement = newLinkedList();
    private final Schema schema = new Schema();

    public ValidatorLanguage() {
        for (Type type : Type.defaultTypes) {
            addSymbol(type.getName(), type);
        }
    }

    @Element(evaluateArgs = false)
    public void element(Symbol name, Block blk) {
        final ElementSchema element = new ElementSchema(name);
        if (currentElement.isEmpty())
            schema.put(element);
        else
            currentElement.peek().addLocalElement(element);
        currentElement.push(element);
        evaluator.eval(blk);
        currentElement.remove();
    }

    @Element
    public void params(List<ParamSpec> args) {
        currentElement.peek().setParams(args);
    }

    @Element
    public ParamSpec val(Type type) {
        return ParamSpec.val(type);
    }

    @Element
    public ParamSpec sym(Type type) {
        return ParamSpec.sym(type);
    }

    @Element
    public ParamSpec variadic(ParamSpec param) {
        return ParamSpec.variadic(param);
    }

    @Element
    public ParamSpec block(ParamSpec param) {
        return ParamSpec.block(param);
    }

    @Element
    public void type(Type type) {
        currentElement.peek().setType(type);
    }

    @Element
    public void defines(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type);
        currentElement.peek().addDefine(def);
    }

    @Element
    public void defines_backward(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type, true);
        currentElement.peek().addDefine(def);
    }

    @Element
    public void defines_global(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type, true);
        currentElement.peek().addDefine(global(def));
    }

    @Element
    public void newtype(@Symbolic Symbol name) {
        evaluator.putSymbol(name, new Type(name));
    }

    @Override
    public Module[] getModules() {
        return new Module[]{this};
    }

    public Schema getReadSchema() {
        return schema;
    }
}
