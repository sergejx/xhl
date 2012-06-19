package xhl.core.validator;

import xhl.core.Error;
import xhl.core.elements.Expression;
import xhl.core.elements.SList;
import xhl.core.elements.SMap;
import xhl.core.elements.Symbol;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Validator for an element can check the validity according to some rules.
 */
public class ElementValidator {
    private final ElementSchema schema;

    public ElementValidator(ElementSchema schema) {
        this.schema = schema;
    }

    /**
     * Get a map with symbols defined forward by the combination and their
     * types.
     *
     * @param args Combination arguments
     * @return A map from defined symbols to their types
     */
    public Map<Symbol, Type> forwardDefinitions(SList args) {
        return definedSymbols(args, true);
    }

    /**
     * Get a map with symbols defined by the element and their types.
     *
     * @param args         Combination arguments
     * @param onlyBackward Get only backward defined symbols
     * @return A map from defined symbols to their types
     */
    private Map<Symbol, Type> definedSymbols(SList args,
                                            boolean onlyBackward) {
        Map<Symbol, Type> symbols = newHashMap();
        for (ElementSchema.DefSpec def : schema.getDefines()) {
            if (onlyBackward && !def.isBackward())
                continue;
            ElementSchema.ParamSpec argspec = schema.getParams().get(def
                    .getArg() - 1);
            if (argspec.getMethod() == ElementSchema.PassingMethod.SYM
                    && argspec.getType().equals(Type.Symbol)) {
                Expression arg = args.get(def.getArg() - 1);
                if (!(arg instanceof Symbol))
                    continue;
                Symbol name = (Symbol) arg;
                Type type = def.getType();
                symbols.put(name, type);
            } else if (argspec.getMethod() == ElementSchema.PassingMethod.SYM
                    && argspec.getType().equals(Type.Map)) {
                Expression arg = args.get(def.getArg() - 1);
                if (!(arg instanceof SMap))
                    continue;
                SMap map = (SMap) arg;
                Type type = def.getType();
                for (Expression key : map.keySet()) {
                    Symbol sym = (Symbol) key;
                    symbols.put(sym, type);
                }
            }
        }
        return symbols;
    }

    /**
     * Check validity of a combination.
     *
     * @param validator The validator
     * @param tail      Arguments of the combination
     * @return Result of the validation
     */
    public ValidationResult check(Validator validator, SList tail) {
        List<Error> errors = newArrayList();
        // Check number of arguments
        int minArgsSize = schema.isVariadic() ? schema
                .getParams().size() - 1 : schema.getParams().size();
        if (tail.size() < minArgsSize
                || (!schema.isVariadic() && tail.size() > minArgsSize)) {
            errors.add(new Error(tail.getPosition(),
                    "Wrong number of arguments"));
            return new ValidationResult(schema.getType(), errors);
        }
        // Check arguments types
        for (int i = 0; i < minArgsSize; i++) {
            errors.addAll(checkArgument(validator, schema.getParams().get(i),
                    tail.get(i)));
        }
        // Variadic arguments
        if (schema.isVariadic()) {
            List<Expression> varargs = tail.subList(schema.getParams()
                    .size(), tail.size());
            ElementSchema.ParamSpec spec = schema.getParams().get
                    (schema.getParams().size() - 1);
            for (Expression arg : varargs)
                errors.addAll(checkArgument(validator, spec, arg));
        }
        return new ValidationResult(schema.getType(), errors,
                definedSymbols(tail, false));
    }

    private List<Error> checkArgument(Validator validator,
                                      ElementSchema.ParamSpec spec,
                                      Expression arg) {
        List<Error> errors = newArrayList();
        Type argtype;
        if (spec.getMethod() == ElementSchema.PassingMethod.SYM)
            argtype = Type.typeOfElement(arg);
        else
            argtype = validator.check(arg);
        if (!argtype.is(spec.getType()))
            errors.add(new Error(arg.getPosition(),
                    "Wrong type of an argument (expected " + spec.getType()
                            + ", found " + argtype + ")"));
        return errors;
    }
}
