package xhl.core.validator;

import java.util.List;

import xhl.core.GenericModule;
import xhl.core.Language;
import xhl.core.Module;
import xhl.core.elements.Block;
import xhl.core.elements.Symbol;
import xhl.core.validator.ElementSchema.DefSpec;
import xhl.core.validator.ElementSchema.ParamSpec;

import static com.google.common.base.Preconditions.checkArgument;

public class ValidatorLanguage extends GenericModule implements Language {

    private ElementSchema currentElement;
    private final Schema schema = new Schema();

    @Function(evaluateArgs = false)
    public void element(Symbol name, Block blk) {
        currentElement = new ElementSchema(name);
        schema.put(currentElement);
        evaluator.eval(blk);
    }

    @Function
    public void params(List<ParamSpec> args) {
        currentElement.setParams(args);
    }

    @Function(evaluateArgs = false)
    public ParamSpec val(Symbol type) {
        return ParamSpec.val(new Type(type));
    }

    @Function(evaluateArgs = false)
    public ParamSpec sym(Symbol type) {
        return ParamSpec.sym(new Type(type));
    }

    @Function
    public ParamSpec variadic(ParamSpec param) {
        return ParamSpec.variadic(param);
    }
    @Function
    public ParamSpec block(ParamSpec param) {
        return ParamSpec.block(param);
    }

    @Function(evaluateArgs = false)
    public void type(Symbol type) {
        currentElement.setType(new Type(type));
    }

    @Function
    public void defines(double arg, @Symbolic Symbol type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, new Type(type));
        currentElement.addDefine(def);
    }

    @Function
    public void defines_backward(double arg, @Symbolic Symbol type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, new Type(type), true);
        currentElement.addDefine(def);
    }

    @Override
    public Module[] getModules() {
        return new Module[] { this };
    }

    public Schema getReadedSchema() {
        return schema;
    }
}
