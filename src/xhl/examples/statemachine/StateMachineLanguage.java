package xhl.examples.statemachine;

import java.io.*;

import xhl.core.Util;
import xhl.core.Language;
import xhl.core.Module;

/**
 * Language for configuring state machine.
 *
 * Based on examples from http://martinfowler.com/dslwip/
 *
 * @author Sergej Chodarev
 */
public class StateMachineLanguage implements Language {

    private final StateMachineModule module = new StateMachineModule();

    @Override
    public Module[] getModules() {
        return new Module[] { module };
    }

    public StateMachine getStateMachine() {
        return module.getStateMachine();
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        if (args.length >= 1) {
            String filename = args[0];
            StateMachineLanguage lang = new StateMachineLanguage();
            Util.execute(lang, filename);
            StateMachine machine = lang.getStateMachine();
            runStateMachine(machine);
        } else
            System.out.println("Give file name as program argument!");
    }

    /**
     * Run state machine interactively on the console.
     *
     * @param machine
     * @throws IOException
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
