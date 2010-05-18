package xhl.core;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import xhl.core.Token.TokenType;
import xhl.core.elements.CodeElement.CodePosition;

/**
 * Lexical analyzer for XHL
 *
 * @author Sergej Chodarev
 */
public class Lexer {
    private static final String PUNCTUATION = "+-*/_=<>.:?!";
    private static final String numberRegex = "[+-]?\\d+(\\.\\d*)?";
    private static final Map<Character, TokenType> simpleTokens =
        new HashMap<Character, TokenType>() {{
            put('(', TokenType.PAR_OPEN);
            put(')', TokenType.PAR_CLOSE);
            put('[', TokenType.BRACKET_OPEN);
            put(']', TokenType.BRACKET_CLOSE);
        }};

    private final Reader input;
    private int ch;
    private int line = 1;
    private int column = 0;

    public Lexer(Reader input) throws IOException {
        this.input = input;
        ch = input.read();
    }

    public Token nextToken() throws IOException {
        while (true) {
            switch (ch) {
            case ' ': // White space
            case '\t':
            case '\n':
            case '\r':
                ch = nextChar();
                break;
            case ';': // Comment
                do
                    ch = nextChar();
                while (ch != '\n');
                break;
            case '"': // String
                return readString();
            default:
                if (simpleTokens.containsKey((char)ch)) {
                    TokenType type = simpleTokens.get((char)ch);
                    ch = nextChar();
                    return new Token(type, getPosition());
                } else if (Character.isLetterOrDigit(ch)
                        || PUNCTUATION.indexOf(ch) != -1)
                    return readSymbol();
                else
                    return null;
            }
        }
    }

    private int nextChar() throws IOException {
        int ch = input.read();
        column++;
        if (ch == '\n') {
            line++;
            column = 0; // FIXME
        }
        return ch;
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        ch = nextChar(); // "
        while (ch != '"') {
            sb.append((char) ch);
            ch = nextChar();
        }
        ch = nextChar(); // "
        return new Token(TokenType.STRING, sb.toString(), getPosition());
    }

    private Token readSymbol() throws IOException {
        CodePosition position = getPosition();

        // Read whole symbol
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(ch) || PUNCTUATION.indexOf(ch) != -1) {
            sb.append((char) ch);
            ch = nextChar();
        }
        String symbol = sb.toString();

        // Check symbol type
        if (symbol.equals("true"))
            return new Token(TokenType.TRUE, position);
        if (symbol.equals("false"))
            return new Token(TokenType.FALSE, position);
        if (Pattern.matches(numberRegex, symbol))
            return new Token(TokenType.NUMBER, Double.valueOf(symbol), position);
        else
            return new Token(TokenType.SYMBOL, symbol, position);
    }

    private CodePosition getPosition() {
        return new CodePosition("input", line, column); // FIXME filename
    }
}
