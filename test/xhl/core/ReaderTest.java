package xhl.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import xhl.core.elements.*;

/**
 * @author Sergej Chodarev
 */
public class ReaderTest {
    private Reader reader;

    @Before
    public void createReader() {
        reader = new Reader();
    }

    @Test
    public void singleLiteral() throws IOException {
        List<Statement> l = reader.read("42");
        LNumber num = (LNumber) l.get(0);
        assertEquals(42.0, num.getValue(), 0);
        assertEquals(1, l.size());
    }

    @Test
    public void list() throws IOException {
        List<Statement> l = reader.read("[1, 2, 3]");
        DataList list = (DataList) l.get(0);
        assertEquals(1, ((LNumber) list.head()).getValue(), 0);
        assertEquals(3, list.size());
    }

    @Test
    public void map() throws IOException {
        List<Statement> l = reader.read("{a: 1, b: 2}");
        LMap map = (LMap) l.get(0);
        assertTrue(map.containsKey(new Symbol("a")));
        assertTrue(map.containsKey(new Symbol("b")));
    }

    @Test
    public void application() throws IOException {
        List<Statement> l = reader.read("foo bar 42");
        CodeList application = (CodeList) l.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("foo", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void infix() throws IOException {
        List<Statement> l = reader.read("foo + bar");
        CodeList application = (CodeList) l.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("+", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void infixRepeating() throws IOException {
        // All operators have the same priority and left associativity
        List<Statement> l = reader.read("foo + bar - spam");

        CodeList application = (CodeList) l.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("-", head.getName());
        assertEquals(3, application.size());

        CodeList subApplication = (CodeList) application.tail().head();
        assertEquals("+", ((Symbol) subApplication.head()).getName());
        assertEquals(3, subApplication.size());
    }

    @Test
    public void parentheses() throws IOException {
        List<Statement> l = reader.read("foo (bar 42)");
        CodeList application = (CodeList) l.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("foo", head.getName());
        assertEquals(2, application.size());
        CodeList arg = (CodeList) application.tail().head();
        assertEquals("bar", ((Symbol) arg.head()).getName());
        assertEquals(2, application.size());
    }

    @Test
    public void block() throws IOException {
        List<Statement> l = reader.read("foo:\n\tbar 42\nspam");
        Block block = (Block) l.get(0);

        Expression head = block.getHead();
        assertEquals("foo", ((Symbol) head).getName());

        List<Statement> body = block.getBody();
        assertEquals(1, body.size());
        CodeList st = (CodeList) body.get(0);
        assertEquals("bar", ((Symbol) st.head()).getName());

        // After block
        Symbol after = (Symbol) l.get(1);
        assertEquals("spam", after.getName());
    }
}
