package xhl.examples.statemachine;

import java.util.*;

/**
 * State
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public class State {

    private final String name;
    private final List<Command> actions = new ArrayList<Command>();
    private final Map<String, Transition> transitions = new HashMap<String, Transition>();

    public State(String name) {
        this.name = name;
    }

    public void addAction(Command e) {
        actions.add(e);
    }

    public String getName() {
        return name;
    }

    public Transition addTransition(Event event, State targetState) {
        return transitions.put(event.getCode(), new Transition(this, event,
                targetState));
    }

    public Collection<State> getAllTargets() {
        List<State> result = new ArrayList<State>();
        for (Transition t : transitions.values()) {
            result.add(t.getTarget());
        }
        return result;
    }

    public boolean hasTransition(String eventCode) {
        return transitions.containsKey(eventCode);
    }

    public State targetState(String eventCode) {
        return transitions.get(eventCode).getTarget();
    }

    public void executeActions(CommandChannel commandsChannel) {
        for (Command c : actions) {
            commandsChannel.send(c.getCode());
        }
    }
}
