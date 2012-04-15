package xhl.core;

import java.io.*;
import java.util.List;

import xhl.core.elements.Block;
import xhl.core.validator.ValidationException;
import xhl.core.validator.Validator;

/**
 * LanguageProcessor is responsible for executing language code.
 *
 * @author Sergej Chodarev
 *
 * FIXME: Needs major rethinking
 */
public class LanguageProcessor {
    private final Language language;
    private final Evaluator evaluator;
    private final Validator validator;

    public LanguageProcessor(Language lang) {
        language = lang;
        evaluator = new Evaluator();
        validator = new Validator();
        for (Module module : language.getModules()) {
            evaluator.loadModule(module);
            validator.addElements(module.getSchema());
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
        execute(new FileReader(file));
    }

    /**
     * Execute code from the input reader
     *
     * @param reader
     * @throws IOException
     * @throws EvaluationException
     */
    public void execute(java.io.Reader reader) throws IOException,
            EvaluationException {
        Block program = Reader.read(reader);
        execute(program);
    }

    /**
     * Validate already parsed code
     *
     * @param program
     *            code to execute
     * @throws EvaluationException
     */
    public List<xhl.core.Error> validate(Block program) throws EvaluationException {
        validator.check(program);
        return validator.getErrors();
    }

    /**
     * Execute already parsed code
     *
     * @param program
     *            code to execute
     * @throws EvaluationException
     */
    public void execute(Block program) throws EvaluationException {
        validator.check(program);
        if (validator.getErrors().isEmpty())
            evaluator.evalAll(program);
        else
            throw new ValidationException(validator.getErrors());
    }

    /**
     * Execute code from file and output error messages to standard error output
     *
     * This is a simplified interface for this class.
     *
     * @param lang
     *            language of the code
     * @param filename
     *            name of the file to execute
     */
    public static void execute(Language lang, String filename) {
        LanguageProcessor processor = new LanguageProcessor(lang);
        try {
            processor.execute(new File(filename));
        } catch (EvaluationException e) {
            System.err.printf("%s: %s\n", e.getPosition(), e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Execute code from input reader and output error messages to standard
     * error output
     *
     * This is a simplified interface for this class.
     *
     * @param lang
     *            language of the code
     * @param reader
     *            input reader
     */
    public static void execute(Language lang, java.io.Reader reader) {
        LanguageProcessor processor = new LanguageProcessor(lang);
        try {
            processor.execute(reader);
        } catch (EvaluationException e) {
            System.err.printf("%s: %s\n", e.getPosition(), e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
