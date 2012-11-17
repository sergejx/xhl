package xhl.examples.statemachine;

/**
 * Base class for events
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public abstract class AbstractEvent {
    private final String name, code;

    public AbstractEvent(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
