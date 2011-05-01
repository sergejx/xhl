package xhl.core;

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Test lexer module.
 *
 * @author Sergej Chodarev
 */
public class LexerTest {
    @Test
    public void testBraces() throws Exception {
        String code = "{[()]}";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.BRACE_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.BRACE_CLOSE, l.nextToken().type);
    }

    @Test
    public void testPunctuation() throws Exception {
        String code = ",:";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.COMMA, l.nextToken().type);
        assertEquals(Token.TokenType.COLON, l.nextToken().type);
    }

    @Test
    public void testSymbols() throws Exception {
        String code = "abc ABc_98";
        Lexer l = new Lexer(new StringReader(code));
        Token t = l.nextToken();
        assertEquals(Token.TokenType.SYMBOL, t.type);
        assertEquals("abc", t.stringValue);
        t = l.nextToken();
        assertEquals(Token.TokenType.SYMBOL, t.type);
        assertEquals("ABc_98", t.stringValue);
    }

    @Test
    public void testOperators() throws Exception {
        String code = "+ ->>";
        Lexer l = new Lexer(new StringReader(code));
        Token t = l.nextToken();
        assertEquals(Token.TokenType.OPERATOR, t.type);
        assertEquals("+", t.stringValue);
        t = l.nextToken();
        assertEquals(Token.TokenType.OPERATOR, t.type);
        assertEquals("->>", t.stringValue);
    }

    @Test
    public void testNumbers() throws Exception {
        String code = "-42 3.14";
        Lexer l = new Lexer(new StringReader(code));
        Token t = l.nextToken();
        assertEquals(Token.TokenType.NUMBER, t.type);
        assertEquals(-42.0, t.doubleValue, 0);
        t = l.nextToken();
        assertEquals(Token.TokenType.NUMBER, t.type);
        assertEquals(3.14, t.doubleValue, 0);
    }

    @Test
    public void testStrings() throws Exception {
        String code = "\"hello\"";
        Lexer l = new Lexer(new StringReader(code));
        Token t = l.nextToken();
        assertEquals(Token.TokenType.STRING, t.type);
        assertEquals("hello", t.stringValue);
    }

    @Test
    public void testKeywords() throws Exception {
        String code = "true false none";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.TRUE, l.nextToken().type);
        assertEquals(Token.TokenType.FALSE, l.nextToken().type);
        assertEquals(Token.TokenType.NONE, l.nextToken().type);
    }

    @Test
    public void testNewline() throws Exception {
        String code = "a\nb\n";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
    }

    @Test
    public void testNewlineInBraces() throws Exception {
        String code = "a\n(b\n[c\n]\n)\n";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
    }

    @Test
    public void testIndentation() throws Exception {
        String code = "a:\n  b\nc\n    d";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.COLON, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.INDENT, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.DEDENT, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.INDENT, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.DEDENT, l.nextToken().type);
    }

    @Test
    public void testCombination() throws Exception {
        String code = "a \"hello\":\n  b [42++true,\n()]\n";
        Lexer l = new Lexer(new StringReader(code));
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.STRING, l.nextToken().type);
        assertEquals(Token.TokenType.COLON, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.INDENT, l.nextToken().type);
        assertEquals(Token.TokenType.SYMBOL, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.NUMBER, l.nextToken().type);
        assertEquals(Token.TokenType.OPERATOR, l.nextToken().type);
        assertEquals(Token.TokenType.TRUE, l.nextToken().type);
        assertEquals(Token.TokenType.COMMA, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_OPEN, l.nextToken().type);
        assertEquals(Token.TokenType.PAR_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.BRACKET_CLOSE, l.nextToken().type);
        assertEquals(Token.TokenType.NEWLINE, l.nextToken().type);
        assertEquals(Token.TokenType.DEDENT, l.nextToken().type);
    }
}
