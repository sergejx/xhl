package sk.tuke.xhl.modules;

import com.google.common.collect.ImmutableMap;
import sk.tuke.xhl.core.EvaluationException;
import sk.tuke.xhl.core.GenericModule;
import sk.tuke.xhl.core.elements.Expression;
import sk.tuke.xhl.core.elements.Symbol;
import sk.tuke.xhl.core.validator.Type;
import sk.tuke.xhl.core.validator.ValidationResult;
import sk.tuke.xhl.core.validator.Validator;

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
