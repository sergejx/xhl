package xhl.examples.statemachine;

import java.io.*;

import xhl.core.Evaluator;
import xhl.core.Reader;
import xhl.core.elements.CodeList;
import xhl.core.exceptions.EvaluationException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException,
            IOException, EvaluationException {
        if (args.length >= 1)
            run(args[0]);
        else
            System.out.println("Give file name as program argument!");
    }

    private static void run(String filename) throws IOException,
            FileNotFoundException, EvaluationException {
        Evaluator evaluator = new Evaluator();
        StateMachineModule module = new StateMachineModule();
        evaluator.loadModule(module);
        Reader reader = new Reader();
        CodeList program = reader.read(new FileReader(filename));
        evaluator.evalAll(program);
        System.out.println(module.getStartState());
        StateMachine machine = module.getStateMachine();
        runStateMachine(machine);
    }

    private static void runStateMachine(StateMachine machine)
            throws IOException {
        Controller ctrl = new Controller(machine, new CommandChannel());
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        String code;
        while (true) {
            System.out.printf("Current state is \"%s\"\n", ctrl
                    .getCurrentState().getName());
            System.out.print("Event? ");

            code = reader.readLine();
            if (code == null)
                break;
            ctrl.handle(code);
        }
        System.out.println("\nBye.");
    }
}
