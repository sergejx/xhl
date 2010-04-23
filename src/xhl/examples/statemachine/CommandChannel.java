package xhl.examples.statemachine;

public class CommandChannel {

    void send(String code) {
        System.out.printf("Action \"%s\" activated.\n", code);
    }

}
