package xhl.core;

import xhl.core.elements.CodeElement.CodePosition;

/**
 * Token from lexical analysis
 */
public class Token {

    public static enum TokenType {
        PAR_OPEN, PAR_CLOSE,
        BRACKET_OPEN, BRACKET_CLOSE,
        BRACE_OPEN, BRACE_CLOSE,
        INDENT, DEDENT,
        COMMA, COLON, LINEEND,
        SYMBOL, OPERATOR,
        STRING, NUMBER,
        TRUE, FALSE, NONE,
    }

    public final TokenType type;
    public final double doubleValue;
    public final String stringValue;
    public final CodePosition position;

    public Token(TokenType t, CodePosition p) {
        this(t, 0, null, p);
    }

    public Token(TokenType t, double n, CodePosition p) {
        this(t, n, null, p);
    }

    public Token(TokenType t, String s, CodePosition p) {
        this(t, 0, s, p);
    }

    private Token(TokenType t, double n, String s, CodePosition p) {
        type = t;
        doubleValue = n;
        stringValue = s;
        position = p;
    }
}
