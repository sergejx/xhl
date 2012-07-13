package xhl.core;

import xhl.core.elements.Block;
import xhl.core.validator.ValidationException;
import xhl.core.validator.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * LanguageProcessor is responsible for executing language code.
 *
 * @author Sergej Chodarev
 */
public class LanguageProcessor {
    private final Module module;
    private final Evaluator evaluator;
    private Validator validator;

    public LanguageProcessor(Module module) {
        if (!module.isLanguage())
            throw new EvaluationException("Executed module is not a language.");
        this.module = module;
        evaluator = new Evaluator();
        evaluator.loadModule(module);
    }

    /**
     * Execute code from the file
     */
    public void execute(File file) throws IOException, EvaluationException {
        execute(new FileReader(file));
    }

    /**
     * Execute code from the input reader
     */
    public void execute(java.io.Reader reader) throws IOException,
            EvaluationException {
        Block program = Reader.read(reader);
        execute(program);
    }

    /**
     * Validate already parsed code
     *
     * @param program code to execute
     * @throws EvaluationException
     */
    public List<xhl.core.Error> validate(Block program)
            throws EvaluationException {
        if (validator == null)
            validator = new Validator(module.getSchema());
        validator.check(program);
        return validator.getErrors();
    }

    /**
     * Execute already parsed code
     *
     * @param program code to execute
     * @throws EvaluationException
     */
    public void execute(Block program) throws EvaluationException {
        List<Error> errors = validate(program);
        if (errors.isEmpty())
            executeWithoutValidation(program);
        else
            throw new ValidationException(errors);
    }

    public void executeWithoutValidation(Block program)
            throws EvaluationException {
        evaluator.evalAll(program);
    }

    /**
     * Execute code from file and output error messages to standard error
     * output.
     * This is a simplified interface for this class.
     *
     * @param module   module of the code
     * @param filename name of the file to execute
     */
    public static void execute(Module module, String filename) {
        LanguageProcessor processor = new LanguageProcessor(module);
        try {
            processor.execute(new File(filename));
        } catch (EvaluationException e) {
            System.err.printf("%s: %s\n", e.getPosition(), e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
