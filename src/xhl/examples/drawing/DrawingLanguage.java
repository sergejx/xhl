package xhl.examples.drawing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import xhl.core.Evaluator;
import xhl.core.Reader;
import xhl.core.elements.CodeList;
import xhl.core.exceptions.EvaluationException;
import xhl.modules.ArithmeticsModule;
import xhl.modules.DefineModule;

public class DrawingLanguage {

    private final Canvas canvas = new Canvas();
    private final Evaluator evaluator;

    public DrawingLanguage() {
        evaluator = new Evaluator();
        evaluator.loadModule(new ArithmeticsModule());
        evaluator.loadModule(new DefineModule());
        evaluator.loadModule(new DrawingModule(canvas));
    }

    public void process(String filename) throws FileNotFoundException,
            IOException {
        Reader reader = new Reader();
        CodeList program = reader.read(new FileReader(filename));
        try {
            evaluator.evalAll(program);
        } catch (EvaluationException e) {
            System.err.printf("%s: %s", e.getPosition(), e);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
