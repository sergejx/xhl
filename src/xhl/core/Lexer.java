package xhl.core;

import java.io.IOException;
import java.io.Reader;

public class Lexer {
    private static final String PUNCTUATION = "+-*/_=<>.:?!";

    public enum TokenType {
        PAR_OPEN, PAR_CLOSE, SYMBOL, STRING, NUMBER;
    }

    public class Token {
        public Token(TokenType t) {
            type = t;
        }

        public Token(TokenType t, double n) {
            type = t;
            doubleValue = n;
        }

        public Token(TokenType t, String s) {
            type = t;
            stringValue = s;
        }

        public TokenType type;
        public double doubleValue;
        public String stringValue;
    }

    private final Reader input;
    private int ch;

    public Lexer(Reader input) throws IOException {
        this.input = input;
        ch = input.read();
    }

    public Token nextToken() throws IOException {
        switch (ch) {
        case ' ':
        case '\t':
        case '\n':
        case '\r':
            ch = input.read();
            return nextToken();
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
