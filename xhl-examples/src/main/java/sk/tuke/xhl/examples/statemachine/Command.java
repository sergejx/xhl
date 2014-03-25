package sk.tuke.xhl.examples.statemachine;

/**
 * Command
 *
 * Example by Martin Fowler: http://martinfowler.com/dslwip/Intro.html
 */
public class Command extends AbstractEvent {

    public Command(String name, String code) {
        super(name, code);
    }

}
