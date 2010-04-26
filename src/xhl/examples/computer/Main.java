package xhl.examples.computer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import xhl.core.Evaluator;
import xhl.core.Reader;
import xhl.core.elements.CodeList;
import xhl.core.exceptions.EvaluationException;

public class Main {

    private static void run(String filename) throws IOException,
            FileNotFoundException, EvaluationException {
        Evaluator evaluator = new Evaluator();
        ComputerModule module = new ComputerModule();
        evaluator.loadModule(module);
        Reader reader = new Reader();
        CodeList program = reader.read(new FileReader(filename));
        evaluator.evalAll(program);
        module.getComputer().print();
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException, EvaluationException {
        if (args.length >= 1)
            run(args[0]);
        else
            System.out.println("Give file name as program argument!");
    }

}
