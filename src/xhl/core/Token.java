package xhl.core;

import xhl.core.elements.Position;

/**
 * Token from lexical analysis
 */
public class Token {

    public static enum TokenType {
        PAR_OPEN, PAR_CLOSE,
        BRACKET_OPEN, BRACKET_CLOSE,
        BRACE_OPEN, BRACE_CLOSE,
        INDENT, DEDENT,
        COMMA, LINEEND,
        SYMBOL, OPERATOR,
        STRING, NUMBER,
        TRUE, FALSE, NONE,
    }

    public final TokenType type;
    public final double doubleValue;
    public final String stringValue;
    public final Position position;

    public Token(TokenType t, Position p) {
        this(t, 0, null, p);
    }

    public Token(TokenType t, double n, Position p) {
        this(t, n, null, p);
    }

    public Token(TokenType t, String s, Position p) {
        this(t, 0, s, p);
    }

    private Token(TokenType t, double n, String s, Position p) {
        type = t;
        doubleValue = n;
        stringValue = s;
        position = p;
    }
}
