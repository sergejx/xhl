package xhl.core.elements;

/**
 * Element of XHL source code
 *
 * @author Sergej Chodarev
 */
public abstract class CodeElement {
    private final CodePosition position;

    public CodeElement() {
        this(null);
    }

    public CodeElement(CodePosition position) {
        this.position = position;
    }

    public CodePosition getPosition() {
        return position;
    }

    /** Element position in source code */
    public static class CodePosition {
        public final String fileName;
        public final int line;
        public final int column;

        public CodePosition() {
            this(null, -1, -1);
        }

        public CodePosition(String fileName, int line, int column) {
            this.fileName = fileName;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("%s:%d:%d", fileName, line, column);
        }
    }
}
