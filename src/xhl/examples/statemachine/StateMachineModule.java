package xhl.examples.statemachine;

import java.util.List;
import java.util.Map;

import xhl.core.GenericModule;
import xhl.core.elements.Block;
import xhl.core.elements.Expression;
import xhl.core.elements.Symbol;
import xhl.core.validator.Type;
import xhl.core.validator.Validator;

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

    @Element(name = "!script")
    public void script(@Symbolic Block exprs) {
        Map<Symbol, Type> defined =
                Validator.backwardDefinitions(exprs, getSchema());
        for (Symbol sym : defined.keySet())
            if (defined.get(sym).isNamed("State"))
                evaluator.putSymbol(sym, new State(sym.getName()));
        evaluator.eval(exprs);
    }

    @Element(evaluateArgs = false)
    public void events(Block blk) throws Exception {
        evaluator.pushEnvironment();
        evaluator.putSymbol(new Symbol(":"), getLocalElement("colonEvent"));
        evaluator.eval(blk);
        evaluator.popEnvironment();
    }

    @Element
    public void resetEvents(List<Event> events) {
        resetEvents = events.toArray(new Event[0]);
    }

    @Element(evaluateArgs = false)
    public void commands(Block blk) throws Exception {
        evaluator.pushEnvironment();
        evaluator.putSymbol(new Symbol(":"), getLocalElement("colonCommand"));
        evaluator.eval(blk);
        evaluator.popEnvironment();
    }

    @Element(local=true)
    public void colonEvent(@Symbolic Symbol name, String code) {
        Event event = new Event(name.getName(), code);
        evaluator.putGlobalSymbol(name, event);
    }

    @Element(local=true)
    public void colonCommand(@Symbolic Symbol name, String code) {
        Command cmd = new Command(name.getName(), code);
        evaluator.putGlobalSymbol(name, cmd);
    }

    @Element(evaluateArgs = false)
    public void state(Symbol name, Block blk) throws Exception {
        currentState = (State) evaluator.getSymbol(name);
        evaluator.eval(blk);
        if (startState == null)
            startState = currentState;
        currentState = null;
    }

    @Element
    public void actions(List<Command> args) throws Exception {
        if (currentState == null)
            throw new Exception("Actions must by defined inside state.");
        for (Command cmd : args) {
            currentState.addAction(cmd);
        }
    }

    @Element(name = "->")
    public void transition(Event trigger, State target) {
        currentState.addTransition(trigger, target);
    }

    // End of DSL functions ==================================================

    public StateMachine getStateMachine() {
        StateMachine machine = new StateMachine(startState);
        machine.addResetEvents(resetEvents);
        return machine;
    }
}
