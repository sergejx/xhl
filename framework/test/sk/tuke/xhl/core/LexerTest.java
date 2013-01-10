package sk.tuke.xhl.core;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static sk.tuke.xhl.core.Token.TokenType.*;

/**
 * Test lexer module.
 *
 * @author Sergej Chodarev
 */
public class LexerTest {
    private static Iterator<Token> makeLexer(String code) throws IOException {
        MaybeError<Iterator<Token>> tokens = Lexer.readTokens(
                new StringReader(code), "<test>");
        return tokens.get();
    }

    @Test
    public void braces() throws Exception {
        String code = "{[()]}";
        Iterator<Token> l = makeLexer(code);
        assertEquals(BRACE_OPEN, l.next().type);
        assertEquals(BRACKET_OPEN, l.next().type);
        assertEquals(PAR_OPEN, l.next().type);
        assertEquals(PAR_CLOSE, l.next().type);
        assertEquals(BRACKET_CLOSE, l.next().type);
        assertEquals(BRACE_CLOSE, l.next().type);
    }

    @Test
    public void punctuation() throws Exception {
        String code = ",";
        Iterator<Token> l = makeLexer(code);
        assertEquals(COMMA, l.next().type);
    }

    @Test
    public void symbols() throws Exception {
        String code = "abc ABc_98";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(SYMBOL, t.type);
        assertEquals("abc", t.stringValue);
        t = l.next();
        assertEquals(SYMBOL, t.type);
        assertEquals("ABc_98", t.stringValue);
    }

    @Test
    public void operators() throws Exception {
        String code = "+ ->>";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(OPERATOR, t.type);
        assertEquals("+", t.stringValue);
        t = l.next();
        assertEquals(OPERATOR, t.type);
        assertEquals("->>", t.stringValue);
    }

    @Test
    public void numbers() throws Exception {
        String code = "-42 3.14";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(NUMBER, t.type);
        assertEquals(-42.0, t.doubleValue, 0);
        t = l.next();
        assertEquals(NUMBER, t.type);
        assertEquals(3.14, t.doubleValue, 0);
    }

    @Test
    public void scientificNumbers() throws Exception {
        String code = "-4.2e15 3.14e-2";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(NUMBER, t.type);
        assertEquals(-4.2e15, t.doubleValue, 0);
        t = l.next();
        assertEquals(NUMBER, t.type);
        assertEquals(3.14e-2, t.doubleValue, 0);
    }

    @Test
    public void strings() throws Exception {
        String code = "\"hello\"";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(STRING, t.type);
        assertEquals("hello", t.stringValue);
    }

    @Test
    public void escapes() throws Exception {
        String code = "\"hello\\n\\t\\\"world\\\"\"";
        Iterator<Token> l = makeLexer(code);
        Token t = l.next();
        assertEquals(STRING, t.type);
        assertEquals("hello\n\t\"world\"", t.stringValue);
    }

    @Test
    public void keywords() throws Exception {
        String code = "true false null";
        Iterator<Token> l = makeLexer(code);
        assertEquals(TRUE, l.next().type);
        assertEquals(FALSE, l.next().type);
        assertEquals(NULL, l.next().type);
    }

    @Test
    public void newline() throws Exception {
        String code = "a\nb\n";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
    }

    @Test
    public void newlineInBraces() throws Exception {
        String code = "a\n(b\n[c\n]\n)\n";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(PAR_OPEN, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(BRACKET_OPEN, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(BRACKET_CLOSE, l.next().type);
        assertEquals(PAR_CLOSE, l.next().type);
        assertEquals(LINE_END, l.next().type);
    }

    @Test
    public void emptyLinesAndComments() throws Exception {
        String code = "a\n\n   #comment\nb";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(EOF, l.next().type);
    }

    @Test
    public void indentation() throws Exception {
        String code = "a:\n  b\nc\n    d";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(OPERATOR, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(INDENT, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(DEDENT, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(INDENT, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(DEDENT, l.next().type);
    }

    @Test
    public void combination() throws Exception {
        String code = "a \"hello\":\n  b [42++true,\n()]\n";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(STRING, l.next().type);
        assertEquals(OPERATOR, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(INDENT, l.next().type);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(BRACKET_OPEN, l.next().type);
        assertEquals(NUMBER, l.next().type);
        assertEquals(OPERATOR, l.next().type);
        assertEquals(TRUE, l.next().type);
        assertEquals(COMMA, l.next().type);
        assertEquals(PAR_OPEN, l.next().type);
        assertEquals(PAR_CLOSE, l.next().type);
        assertEquals(BRACKET_CLOSE, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(DEDENT, l.next().type);
    }

    @Test
    public void eof() throws Exception {
        String code = "a";
        Iterator<Token> l = makeLexer(code);
        assertEquals(SYMBOL, l.next().type);
        assertEquals(LINE_END, l.next().type);
        assertEquals(EOF, l.next().type);
    }

    @Test
    public void testErrors() throws Exception {
        String code = "\"hello";
        MaybeError<Iterator<Token>> tokens = Lexer.readTokens(
                new StringReader(code), "<test>");
        assertEquals(1, tokens.getErrors().size());
    }
}
