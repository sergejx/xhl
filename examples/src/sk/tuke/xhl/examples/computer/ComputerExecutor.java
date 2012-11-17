package sk.tuke.xhl.examples.computer;

import sk.tuke.xhl.core.LanguageProcessor;

import java.io.IOException;

/**
 * Executor of the simple computer configuration language.
 *
 * @author Sergej Chodarev
 */
public class ComputerExecutor {

    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            String filename = args[0];
            ComputerModule module = new ComputerModule();
            LanguageProcessor.execute(module, filename);
            module.getComputer().print();
        } else
            System.out.println("Give file name as program argument!");
    }

}
