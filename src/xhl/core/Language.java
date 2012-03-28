package xhl.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * XHL based language
 *
 * @author Sergej Chodarev
 */
public abstract class Language {
    /**
     * Get modules from which the language is composed.
     *
     * @return language modules
     */
    public abstract Module[] getModules();

    /**
     * Execute code from file and output error messages to standard error output
     *
     * @param lang
     *            language of the code
     * @param filename
     *            name of the file to execute
     */
    public void execute(String filename) {
        LanguageProcessor processor = new LanguageProcessor(this);
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
