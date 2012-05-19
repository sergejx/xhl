package xhl.core.validator;

import java.util.List;
import java.util.Map;

import xhl.core.Error;
import xhl.core.elements.Symbol;

class ValidationResult {
    public final Type type;
    public final List<Error> errors;
    public Map<Symbol, Type> defined;

    public ValidationResult(Type type, List<Error> errors) {
        this.type = type;
        this.errors = errors;
    }
}