package xhl.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import xhl.core.exceptions.EvaluationException;

/**
 * Utility functions
 *
 * @author Sergej Chodarev
 */
public final class Util {

    /**
     * Execute code from file and output error messages to standard error output
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
