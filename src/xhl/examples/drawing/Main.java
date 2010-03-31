package xhl.examples.drawing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

import xhl.core.Evaluator;
import xhl.core.Reader;
import xhl.core.exceptions.EvaluationException;
import xhl.modules.ArithmeticsModule;
import xhl.modules.DefineModule;

public class Main extends JFrame {

    private final Canvas canvas = new Canvas();

    public Main() {
        super("XHL drawing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        add(canvas);
    }

    private void run(String filename) throws IOException,
            FileNotFoundException, EvaluationException {
        Evaluator evaluator = new Evaluator();
        evaluator.loadModule(new ArithmeticsModule(evaluator));
        evaluator.loadModule(new DefineModule(evaluator));
        DrawingModule module = new DrawingModule(evaluator, canvas);
        evaluator.loadModule(module);

        Reader reader = new Reader();
        List<Object> program = reader.read(new FileReader(filename));
        evaluator.evalAll(program);

        setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException, EvaluationException {
        if (args.length >= 1)
            new Main().run(args[0]);
        else
            System.out.println("Give file name as program argument!");
    }

}
