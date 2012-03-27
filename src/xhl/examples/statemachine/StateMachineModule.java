package xhl.examples.statemachine;

import xhl.core.GenericModule;
import xhl.core.elements.*;

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
    public void events(Block blk) throws Exception {
        for (Expression stmt : blk) {
            try {
                Combination def = (Combination) stmt;
                Symbol sym = (Symbol) def.get(0);
                LString code = (LString) def.get(1);
                Event event = new Event(sym.getName(), code.getValue());
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
    public void commands(Block blk) throws Exception {
        for (Expression expr : blk) {
            try {
                Combination def = (Combination) expr;
                Symbol sym = (Symbol) def.get(0);
                LString code = (LString) def.get(1);
                Command cmd = new Command(sym.getName(), code.getValue());
                evaluator.putSymbol(sym, cmd);
            } catch (ClassCastException e) {
                throw new Exception("Wrong type of argument.");
            }
        }
    }

    @Function(evaluateArgs = false)
    public void state(Symbol name, Block blk) throws Exception {
        currentState = getState(name);
        for (Expression expr : blk) {
            evaluator.eval(expr);
        }
        if (startState == null)
            startState = currentState;
        currentState = null;
    }

    @Function
    public void actions(Command... args) throws Exception {
        if (currentState == null)
            throw new Exception("Actions must by defined inside state.");
        for (Command cmd : args) {
            currentState.addAction(cmd);
        }
    }

    @Function(name = "->", evaluateArgs = false)
    public void transition(Symbol trig, Symbol targ) throws Exception {
        try {
            Event trigger = (Event) evaluator.getSymbol(trig);
            State target = getState(targ);
            currentState.addTransition(trigger, target);
        } catch (ClassCastException e) {
            throw new Exception(String.format(
                    "Symbol '%s' does not represent event.", trig));
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
}
