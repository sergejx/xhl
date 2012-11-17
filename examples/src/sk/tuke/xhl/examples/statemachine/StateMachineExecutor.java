package sk.tuke.xhl.examples.statemachine;

import sk.tuke.xhl.core.LanguageProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An executor for state machine language.
 *
 * @author Sergej Chodarev
 */
public class StateMachineExecutor {

    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            String filename = args[0];
            StateMachineModule module = new StateMachineModule();
            LanguageProcessor.execute(module, filename);
            StateMachine machine = module.getStateMachine();
            runStateMachine(machine);
        } else
            System.out.println("Give file name as program argument!");
    }

    /**
     * Run state machine interactively on the console.
     */
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
