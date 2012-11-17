package xhl.examples.statemachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * State machine
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public class StateMachine {

    private final State start;

    private final List<Event> resetEvents = new ArrayList<>();

    public StateMachine(State start) {
        this.start = start;
    }

    public State getStart() {
        return start;
    }

    public Collection<State> getStates() {
        List<State> result = new ArrayList<>();
        gatherForwards(result, start);
        return result;
    }

    private void gatherForwards(Collection<State> result, State start) {
        if (start != null && !result.contains(start)) {
            result.add(start);
            for (State next : start.getAllTargets()) {
                gatherForwards(result, next);
            }
        }
    }

    public void addResetEvents(Event... events) {
        Collections.addAll(resetEvents, events);
    }

    public boolean isResetEvent(String eventCode) {
        return resetEventCodes().contains(eventCode);
    }

    private List<String> resetEventCodes() {
        List<String> result = new ArrayList<>();
        for (Event e : resetEvents) {
            result.add(e.getCode());
        }
        return result;
    }
}
