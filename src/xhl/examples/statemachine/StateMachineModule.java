package xhl.examples.statemachine;

import xhl.core.GenericModule;
import xhl.core.elements.CodeElement;
import xhl.core.elements.LString;
import xhl.core.elements.Symbol;

/**
 * XHL module for configuring state machine
 *
 * @author Sergej Chodarev
 */
public class StateMachineModule extends GenericModule {
    private Event[] resetEvents = null;
    private State startState = null;
    private State currentState = null;

    // DSL functions =========================================================

    @Function(evaluateArgs = false)
    public void events(CodeElement... args) throws Exception {
        if (args.length % 2 != 0)
            throw new Exception("Function needs even number of arguments.");
        for (int i = 0; i < args.length; i += 2) {
            try {
                Symbol sym = (Symbol) args[i];
                String code = ((LString) args[i + 1]).getValue();
                Event event = new Event(sym.getName(), code);
                evaluator.putSymbol(sym, event);
            } catch (ClassCastException e) {
                throw new Exception("Wrong type of argument.");
            }
        }
    }

    @Function
    public void resetEvents(Event... events) {
        resetEvents = events;
    }

    @Function(evaluateArgs = false)
    public void commands(CodeElement... args) throws Exception {
        if (args.length % 2 != 0)
            throw new Exception();
        for (int i = 0; i < args.length; i += 2) {
            try {
                Symbol sym = (Symbol) args[i];
                String code = ((LString) args[i + 1]).getValue();
                Command cmd = new Command(sym.getName(), code);
                evaluator.putSymbol(sym, cmd);
            } catch (ClassCastException e) {
                throw new Exception("Wrong type of argument.");
            }
        }
    }

    @Function(evaluateArgs = false)
    public void state(Symbol name, CodeElement... args) throws Exception {
        currentState = getState(name);
        for (CodeElement element : args) {
            evaluator.eval(element);
        }
        if (startState == null)
            startState = currentState;
        currentState = null;
    }

    @Function(evaluateArgs = false)
    public void actions(Symbol... args) throws Exception {
        for (Symbol symbol : args) {
            try {
                currentState.addAction((Command) evaluator.getSymbol(symbol));
            } catch (ClassCastException e) {
                throw new Exception(String.format(
                        "Symbol '%s' does not represent command.", symbol));
            }
        }
    }

    @Function(evaluateArgs = false)
    public void transitions(Symbol... args) throws Exception {
        for (int i = 0; i < args.length; i += 2) {
            try {
                Event trigger = (Event) evaluator.getSymbol(args[i]);
                State target = getState(args[i + 1]);
                currentState.addTransition(trigger, target);
            } catch (ClassCastException e) {
                throw new Exception(String.format(
                        "Symbol '%s' does not represent event.", args[i]));
            }
        }
    }

    // End of DSL functions ==================================================

    private State getState(Symbol symbol) {
        State state = (State) evaluator.getSymbol(symbol);
        if (state == null) {
            state = new State(symbol.getName());
            evaluator.putSymbol(symbol, state);
        }
        return state;
    }

    public StateMachine getStateMachine() {
        StateMachine machine = new StateMachine(startState);
        machine.addResetEvents(resetEvents);
        return machine;
    }

    public State getStartState() {
        return startState;
    }
}
