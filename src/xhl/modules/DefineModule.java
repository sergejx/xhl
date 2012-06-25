package xhl.modules;

import com.google.common.collect.ImmutableMap;
import xhl.core.EvaluationException;
import xhl.core.GenericModule;
import xhl.core.elements.Expression;
import xhl.core.elements.Symbol;
import xhl.core.validator.Type;
import xhl.core.validator.ValidationResult;
import xhl.core.validator.Validator;

public class DefineModule extends GenericModule {

    @Element
    public void define(@Symbolic Symbol symbol, Object value)
            throws EvaluationException {
        if (evaluator.hasSymbol(symbol))
            throw new EvaluationException(String.format(
                    "Symbol '%s' is already defined", symbol));
        evaluator.putSymbol(symbol, value);
    }

    @Check(name="define")
    public ValidationResult checkDefine(Validator validator,
                                        Symbol symbol,
                                        Expression value) {
        Type type = validator.check(value);
        return new ValidationResult(Type.Null, null,
                ImmutableMap.of(symbol, type));
    }
}
