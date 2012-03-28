package xhl.core;

import java.io.*;

import xhl.core.elements.Block;

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

        Block program = reader.read(new FileReader(file));
        execute(program);
    }

    /**
     * Execute already parsed code
     *
     * @param program
     *            code to execute
     * @throws EvaluationException
     */
    public void execute(Block program) throws EvaluationException {
        evaluator.evalAll(program);
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
}
