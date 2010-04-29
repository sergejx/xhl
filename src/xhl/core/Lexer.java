package xhl.core;

import java.io.IOException;
import java.io.Reader;

import xhl.core.Token.TokenType;
import xhl.core.elements.CodeElement.CodePosition;

/**
 * Lexical analyzer for XHL
 *
 * @author Sergej Chodarev
 */
public class Lexer {
    private static final String PUNCTUATION = "+-*/_=<>.:?!";

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
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                ch = nextChar();
                break;
            case ';':
                do
                    ch = nextChar();
                while (ch != '\n');
                break;
            case '(':
                ch = nextChar();
                return new Token(TokenType.PAR_OPEN, getPosition());
            case ')':
                ch = nextChar();
                return new Token(TokenType.PAR_CLOSE, getPosition());
            case '[':
                ch = nextChar();
                return new Token(TokenType.BRACKET_OPEN, getPosition());
            case ']':
                ch = nextChar();
                return new Token(TokenType.BRACKET_CLOSE, getPosition());
            case '"':
                return readString();
            default:
                if (Character.isDigit(ch))
                    return readDouble();
                else if (Character.isLetter(ch)
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

    private Token readDouble() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(ch)) {
            sb.append((char) ch);
            ch = nextChar();
        }
        if (ch == '.') {
            sb.append((char) ch);
            ch = nextChar();
            while (Character.isDigit(ch)) {
                sb.append((char) ch);
                ch = nextChar();
            }
        }
        return new Token(TokenType.NUMBER, Double.valueOf(sb.toString()),
                getPosition());
    }

    private Token readSymbol() throws IOException {
        CodePosition position = getPosition();
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(ch) || PUNCTUATION.indexOf(ch) != -1) {
            sb.append((char) ch);
            ch = nextChar();
        }
        String symbol = sb.toString();
        if (symbol.equals("true"))
            return new Token(TokenType.TRUE, position);
        if (symbol.equals("false"))
            return new Token(TokenType.FALSE, position);
        else
            return new Token(TokenType.SYMBOL, symbol, position);
    }

    private CodePosition getPosition() {
        return new CodePosition("input", line, column); // FIXME filename
    }
}
