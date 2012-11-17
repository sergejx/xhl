package xhl.examples.statemachine;

/**
 * State machine controller
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public class Controller {

    private State currentState;
    private final StateMachine machine;

    protected CommandChannel commandsChannel;

    public Controller(StateMachine machine, CommandChannel channel) {
        this.machine = machine;
        this.commandsChannel = channel;
        transitionTo(machine.getStart());
    }

    public CommandChannel getCommandChannel() {
        return commandsChannel;
    }

    public void handle(String eventCode) {
        if (currentState.hasTransition(eventCode)) {
            transitionTo(currentState.targetState(eventCode));
        } else if (machine.isResetEvent(eventCode)) {
            transitionTo(machine.getStart());
        }
        // ignore unknown events
    }

    private void transitionTo(State target) {
        currentState = target;
        currentState.executeActions(commandsChannel);
    }

    public State getCurrentState() {
        return currentState;
    }
}
