package xhl.core.elements;

/** Element position in source code */
public class Position {
    public final String fileName;
    public final int line;
    public final int column;

    public Position() {
        this(null, -1, -1);
    }

    public Position(String fileName, int line, int column) {
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%d", fileName, line, column);
    }
}