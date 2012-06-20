package xhl.core.validator;

import xhl.core.elements.SList;
import xhl.core.elements.Symbol;

import java.util.Map;

/**
 * An ElementValidator is responsible for checking validity of combinations
 * starting with some specific symbol (or of the symbol itself).
 */
public interface ElementValidator {
    /**
     * Get a map with symbols defined forward by a combination.
     *
     * @param args Combination arguments
     * @return A map from defined symbols to their types
     */
    Map<Symbol, Type> forwardDefinitions(SList args);

    /**
     * Check validity of a combination.
     *
     * @param validator The validator
     * @param tail      Arguments of the combination
     * @return Result of the validation
     */
    ValidationResult check(Validator validator, SList tail);
}
