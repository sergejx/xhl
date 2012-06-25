package xhl.examples.computer;

import java.io.FileNotFoundException;
import java.io.IOException;

import xhl.core.Language;
import xhl.core.LanguageProcessor;
import xhl.core.Module;

/**
 * Simple computer configuration language.
 *
 * Inspired by examples from http://martinfowler.com/dslwip/
 *
 * @author Sergej Chodarev
 */
public class ComputerLanguage implements Language {

    private static final ComputerModule module = new ComputerModule();

    @Override
    public Module[] getModules() {
        return new Module[] { module };
    }

    /** Get computer configured by the script */
    public Computer getComputer() {
        return module.getComputer();
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            String filename = args[0];
            ComputerLanguage lang = new ComputerLanguage();
            LanguageProcessor.execute(lang, filename);
            lang.getComputer().print();
        } else
            System.out.println("Give file name as program argument!");
    }

}
