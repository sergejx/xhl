package xhl.core;

import xhl.core.elements.CodeList;
import xhl.core.exceptions.EvaluationException;

/**
 * LanguageProcessor is responsible for executing language code.
 *
 * @author Sergej Chodarev
 */
abstract public class LanguageProcessor {
    private final Language language;
    private final Evaluator evaluator;

    public LanguageProcessor(Language lang) {
        language = lang;
        evaluator = new Evaluator();
        for (Module module : language.getModules()) {
            evaluator.loadModule(module);
        }
    }

    protected void execute(CodeList program) throws EvaluationException {
        evaluator.evalAll(program);
    }
}
