package xhl.core.validator;

import xhl.core.Error;
import xhl.core.elements.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationResult {
    private final Type type;
    private final List<Error> errors;
    private final Map<Symbol, Type> defined;
    private final Map<Symbol, Type> definedGlobal;

    public ValidationResult(Type type, List<Error> errors) {
        this(type, errors, Collections.<Symbol, Type>emptyMap());
    }

    public ValidationResult(Type type, List<Error> errors,
                            Map<Symbol, Type> defined) {
        this(type, errors != null ? errors : Collections.<Error>emptyList(),
                defined, Collections.<Symbol, Type>emptyMap());
    }

    public ValidationResult(Type type, List<Error> errors,
                            Map<Symbol, Type> defined,
                            Map<Symbol, Type> definedGlobal) {
        this.type = type;
        this.errors = errors != null ? errors : Collections.<Error>emptyList();
        this.defined = defined;
        this.definedGlobal = definedGlobal;
    }

    public Type getType() {
        return type;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Map<Symbol, Type> getDefined() {
        return defined;
    }

    public Map<Symbol, Type> getDefinedGlobal() {
        return definedGlobal;
    }
}