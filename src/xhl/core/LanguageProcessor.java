package xhl.core;

import java.io.*;
import java.util.List;

import xhl.core.elements.Expression;
import xhl.core.exceptions.EvaluationException;

/**
 * LanguageProcessor is responsible for executing language code.
 *
 * @author Sergej Chodarev
 */
public class LanguageProcessor {
    private final Language language;
    private final Reader reader;
    private final Evaluator evaluator;

    public LanguageProcessor(Language lang) {
        language = lang;
        reader = new Reader();
        evaluator = new Evaluator();
        for (Module module : language.getModules()) {
            evaluator.loadModule(module);
        }
    }

    /**
     * Execute code from the file
     *
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws EvaluationException
     */
    public void execute(File file) throws FileNotFoundException, IOException,
            EvaluationException {

        List<Expression> program = reader.read(new FileReader(file));
        execute(program);
    }

    /**
     * Execute already parsed code
     *
     * @param program
     *            code to execute
     * @throws EvaluationException
     */
    public void execute(List<Expression> program) throws EvaluationException {
        evaluator.evalAll(program);
    }
}
