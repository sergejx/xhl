package xhl.core;

import java.io.IOException;
import java.io.Reader;

/**
 * Lexical analyzer for XHL
 *
 * @author Sergej Chodarev
 */
public class Lexer {
    private static final String PUNCTUATION = "+-*/_=<>.:?!";

    public enum TokenType {
        PAR_OPEN, PAR_CLOSE, SYMBOL, STRING, NUMBER;
    }

    /**
     * Token from lexical analysis
     */
    public class Token {
        public final TokenType type;
        public final double doubleValue;
        public final String stringValue;

        public Token(TokenType t) {
            type = t;
            doubleValue = 0;
            stringValue = null;
        }

        public Token(TokenType t, double n) {
            type = t;
            doubleValue = n;
            stringValue = null;
        }

        public Token(TokenType t, String s) {
            type = t;
            stringValue = s;
            doubleValue = 0;
        }
    }

    private final Reader input;
    private int ch;

    public Lexer(Reader input) throws IOException {
        this.input = input;
        ch = input.read();
    }

    public Token nextToken() throws IOException {
        while (true) {
            switch (ch) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                ch = input.read();
                break;
            case ';':
                do ch = input.read();
                while (ch != '\n');
                break;
            case '(':
                ch = input.read();
                return new Token(TokenType.PAR_OPEN);
            case ')':
                ch = input.read();
                return new Token(TokenType.PAR_CLOSE);
            case '"':
                return readString();
            default:
                if (Character.isDigit(ch))
                    return readDouble();
                else if (Character.isLetter(ch) || PUNCTUATION.indexOf(ch) != -1)
                    return readSymbol();
                else
                    return null;
            }
        }
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        ch = input.read(); // "
        while (ch != '"') {
            sb.append((char) ch);
            ch = input.read();
        }
        ch = input.read(); // "
        return new Token(TokenType.STRING, sb.toString());
    }

    private Token readDouble() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(ch)) {
            sb.append((char) ch);
            ch = input.read();
        }
        if (ch == '.') {
            sb.append((char) ch);
            ch = input.read();
            while (Character.isDigit(ch)) {
                sb.append((char) ch);
                ch = input.read();
            }
        }
        return new Token(TokenType.NUMBER, Double.valueOf(sb.toString()));
    }

    private Token readSymbol() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(ch) || PUNCTUATION.indexOf(ch) != -1) {
            sb.append((char) ch);
            ch = input.read();
        }
        return new Token(TokenType.SYMBOL, sb.toString());
    }
}
