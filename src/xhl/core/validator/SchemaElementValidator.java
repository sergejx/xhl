package xhl.core.validator;

import xhl.core.Error;
import xhl.core.elements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validator for an element that checks the validity according to rules
 * specified in the language schema.
 */
class SchemaElementValidator implements ElementValidator {
    private final ElementSchema schema;

    public SchemaElementValidator(ElementSchema schema) {
        this.schema = schema;
    }

    /**
     * Get a map with symbols defined forward by a combination.
     *
     * @param args Combination arguments
     * @return A map from defined symbols to their types
     */
    @Override
    public Map<Symbol, Type> forwardDefinitions(SList args) {
        return definedSymbols(args, true).local;
    }

    /**
     * Get a map with symbols defined by the element and their types.
     *
     * @param args         Combination arguments
     * @param onlyBackward Get only backward defined symbols
     * @return A map from defined symbols to their types
     */
    private DefinedSymbols definedSymbols(SList args,
                                          boolean onlyBackward) {
        Map<Symbol, Type> symbols = new HashMap<>();
        Map<Symbol, Type> globalSymbols = new HashMap<>();
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
                if (def.isGlobal())
                    globalSymbols.put(name, type);
                else
                    symbols.put(name, type);
            } else if (argspec.getMethod() == ElementSchema.PassingMethod.SYM
                    && argspec.getType().equals(Type.Map)) {
                // If an element representing defined name is not a symbol,
                // but a map, then every key of the map defines a new name.
                Expression arg = args.get(def.getArg() - 1);
                if (!(arg instanceof SMap))
                    continue;
                SMap map = (SMap) arg;
                Type type = def.getType();
                for (Expression key : map.keySet()) {
                    Symbol sym = (Symbol) key;
                    if (def.isGlobal())
                        globalSymbols.put(sym, type);
                    else
                        symbols.put(sym, type);
                }
            }
        }
        return new DefinedSymbols(symbols, globalSymbols);
    }

    /**
     * Check validity of a combination.
     *
     * @param validator The validator
     * @param tail      Arguments of the combination
     * @return Result of the validation
     */
    @Override
    public ValidationResult check(Validator validator, SList tail) {
        List<Error> errors = new ArrayList<>();
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
        DefinedSymbols definedSymbols = definedSymbols(tail, false);
        return new ValidationResult(schema.getType(), errors,
                definedSymbols.local, definedSymbols.global);
    }

    private List<Error> checkArgument(Validator validator,
                                      ElementSchema.ParamSpec spec,
                                      Expression arg) {
        List<Error> errors = new ArrayList<>();
        Type argtype;
        if (spec.getMethod() == ElementSchema.PassingMethod.SYM)
            argtype = Type.typeOfElement(arg);
        else {
            if (arg instanceof Block)
                argtype = validator.checkWithLocalScope(arg,
                        schema.getLocalElements());
            else
                argtype = validator.check(arg);
        }
        if (!argtype.is(spec.getType()))
            errors.add(new Error(arg.getPosition(),
                    "Wrong type of an argument (expected " + spec.getType()
                            + ", found " + argtype + ")"));
        return errors;
    }

    private class DefinedSymbols {
        public final Map<Symbol, Type> local;
        public final Map<Symbol, Type> global;

        private DefinedSymbols(Map<Symbol, Type> local,
                               Map<Symbol, Type> global) {
            this.local = local;
            this.global = global;
        }
    }
}
