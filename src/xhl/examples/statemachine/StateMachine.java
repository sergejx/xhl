package xhl.examples.statemachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * State machine
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public class StateMachine {

    private final State start;

    private final List<Event> resetEvents = new ArrayList<Event>();

    public StateMachine(State start) {
        this.start = start;
    }

    public State getStart() {
        return start;
    }

    public Collection<State> getStates() {
        List<State> result = new ArrayList<State>();
        gatherForwards(result, start);
        return result;
    }

    private void gatherForwards(Collection<State> result, State start) {
        if (start == null) {
            return;
        }
        if (result.contains(start)) {
            return;
        } else {
            result.add(start);
            for (State next : start.getAllTargets()) {
                gatherForwards(result, next);
            }
            return;
        }
    }

    public void addResetEvents(Event... events) {
        for (Event e : events) {
            resetEvents.add(e);
        }
    }

    public boolean isResetEvent(String eventCode) {
        return resetEventCodes().contains(eventCode);
    }

    private List<String> resetEventCodes() {
        List<String> result = new ArrayList<String>();
        for (Event e : resetEvents) {
            result.add(e.getCode());
        }
        return result;
    }
}
