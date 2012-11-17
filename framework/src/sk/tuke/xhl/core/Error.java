package sk.tuke.xhl.core;

import sk.tuke.xhl.core.elements.Position;

public class Error {
    public final Position position;
    public final String message;

    public Error(Position position, String message) {
        this.position = position;
        this.message = message;
    }
}
