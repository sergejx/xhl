package xhl.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
        Block prg = reader.read("42");
        LNumber num = (LNumber) prg.get(0);
        assertEquals(42.0, num.getValue(), 0);
        assertEquals(1, prg.size());
    }

    @Test
    public void list() throws IOException {
        Block prg = reader.read("[1, 2, 3]");
        LList list = (LList) prg.get(0);
        assertEquals(1, ((LNumber) list.get(0)).getValue(), 0);
        assertEquals(3, list.size());
    }

    @Test
    public void map() throws IOException {
        Block prg = reader.read("{a: 1, b: 2}");
        LMap map = (LMap) prg.get(0);
        assertTrue(map.containsKey(new Symbol("a")));
        assertTrue(map.containsKey(new Symbol("b")));
    }

    @Test
    public void application() throws IOException {
        Block prg = reader.read("foo bar 42");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("foo", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void infix() throws IOException {
        Block prg = reader.read("foo + bar");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("+", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void infixRepeating() throws IOException {
        // All operators have the same priority and left associativity
        Block prg = reader.read("foo + bar - spam");

        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("-", head.getName());
        assertEquals(3, application.size());

        Combination subApplication = (Combination) application.get(1);
        assertEquals("+", ((Symbol) subApplication.head()).getName());
        assertEquals(3, subApplication.size());
    }

    @Test
    public void parentheses() throws IOException {
        Block prg = reader.read("foo (bar 42)");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("foo", head.getName());
        assertEquals(2, application.size());
        Combination arg = (Combination) application.get(1);
        assertEquals("bar", ((Symbol) arg.head()).getName());
        assertEquals(2, application.size());
    }

    @Test
    public void block() throws IOException {
        Block prg = reader.read("foo:\n\tbar 42\nspam");
        Expression exp = prg.get(0);

        // Header
        assertTrue(exp instanceof Combination);
        Combination blkExp = (Combination) exp;
        assertEquals(2, blkExp.size());
        Expression head = blkExp.head();
        assertEquals("foo", ((Symbol) head).getName());

        // Body
        Expression blk = blkExp.get(1);
        assertTrue(blk instanceof Block);
        Block block = (Block) blk;
        assertEquals(1, block.size());
        Combination st = (Combination) block.get(0);
        assertEquals("bar", ((Symbol) st.head()).getName());

        // After block
        Symbol after = (Symbol) prg.get(1);
        assertEquals("spam", after.getName());
    }
}
