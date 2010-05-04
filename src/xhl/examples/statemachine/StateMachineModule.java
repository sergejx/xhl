package xhl.examples.statemachine;

import java.util.*;

import xhl.core.GenericModule;
import xhl.core.elements.*;

/**
 * XHL module for configuring state machine
 *
 * @author Sergej Chodarev
 */
public class StateMachineModule extends GenericModule {
    private final Symbol actionsSection = new Symbol("actions");
    private final Symbol transitionsSection = new Symbol("transitions");

    private final List<Event> resetEvents = new ArrayList<Event>();
    private State startState = null;

    // DSL functions =========================================================

    @Function(evaluateArgs = false)
    public void events(CodeElement... args) throws Exception {
        if (args.length % 2 != 0)
            throw new Exception();
        for (int i = 0; i < args.length; i += 2) { // FIXME: catch
            // ClassCastException
            Symbol sym = (Symbol) args[i];
            String code = ((LString) args[i + 1]).getValue();
            Event event = new Event(sym.getName(), code);
            evaluator.putSymbol(sym, event);
        }
    }

    @Function
    public void resetEvents(Event... events) {
        for (Event event : events) {
            resetEvents.add(event);
        }
    }

    @Function(evaluateArgs = false)
    public void commands(CodeElement... args) throws Exception {
        if (args.length % 2 != 0)
            throw new Exception();
        for (int i = 0; i < args.length; i += 2) { // FIXME: catch
            // ClassCastException
            Symbol sym = (Symbol) args[i];
            String code = ((LString) args[i + 1]).getValue();
            Command cmd = new Command(sym.getName(), code);
            evaluator.putSymbol(sym, cmd);
        }
    }

    @Function(evaluateArgs = false)
    public void state(CodeElement... args) throws Exception {
        try {
            Symbol stateSym = (Symbol) args[0];
            Command[] actions = {};
            Map<Event, State> transitions = null;
            int i = 1;
            if (((Symbol) args[i]).equals(actionsSection)) {
                actions = processActions((CodeList) args[i + 1]);
                i += 2;
            }
            if (((Symbol) args[i]).equals(transitionsSection)) {
                transitions = processTransitions((CodeList) args[i + 1]);
            }
            addState(stateSym, actions, transitions);
        } catch (ClassCastException e) {
            throw new Exception();
        }
    }

    // End of DSL functions ==================================================

    private Command[] processActions(CodeList args) {
        Command[] actions = new Command[args.size()];
        int i = 0;
        for (Object obj: args) {
            actions[i++] = (Command) evaluator.getSymbol((Symbol) obj);
        }
        return actions;
    }

    private Map<Event, State> processTransitions(CodeList args) {
        Map<Event, State> transitions = new HashMap<Event, State>();

        for (Iterator<Object> i = args.iterator(); i.hasNext(); ) {
            Symbol trigger = (Symbol) i.next();
            i.next(); // =>
            Symbol target = (Symbol) i.next();

            Event event = (Event) evaluator.getSymbol(trigger);
            State state = getState(target);
            transitions.put(event, state);
        }
        return transitions;
    }

    private State getState(Symbol symbol) {
        State state = (State) evaluator.getSymbol(symbol);
        if (state == null) {
            state = new State(symbol.getName());
            evaluator.putSymbol(symbol, state);
        }
        return state;
    }

    private void addState(Symbol sym, Command[] actions,
            Map<Event, State> transitions) {
        State state = getState(sym);
        for (Command action : actions) {
            state.addAction(action);
        }
        if (transitions != null) {
            for (Event event : transitions.keySet()) {
                state.addTransition(event, transitions.get(event));
            }
        }
        if (startState == null)
            startState = state;
    }

    public StateMachine getStateMachine() {
        return new StateMachine(startState);
    }

    public State getStartState() {
        return startState;
    }
}
