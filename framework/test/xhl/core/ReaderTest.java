package xhl.core;

import java.io.IOException;

import org.junit.Test;

import xhl.core.elements.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Sergej Chodarev
 */
public class ReaderTest {

    @Test
    public void singleLiteral() throws IOException {
        Block prg = Reader.read("42");
        SNumber num = (SNumber) prg.get(0);
        assertEquals(42.0, num.getValue(), 0);
        assertEquals(1, prg.size());
    }

    @Test
    public void list() throws IOException {
        Block prg = Reader.read("[1, 2, 3]");
        SList list = (SList) prg.get(0);
        assertEquals(1.0, ((SNumber) list.get(0)).getValue(), 0);
        assertEquals(3, list.size());
    }

    @Test
    public void map() throws IOException {
        Block prg = Reader.read("{a: 1, b: 2}");
        SMap map = (SMap) prg.get(0);
        assertTrue(map.containsKey(new Symbol("a")));
        assertTrue(map.containsKey(new Symbol("b")));
    }

    @Test
    public void application() throws IOException {
        Block prg = Reader.read("foo bar 42");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("foo", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void infix() throws IOException {
        Block prg = Reader.read("foo + bar");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("+", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void prefixOperator() throws IOException {
        Block prg = Reader.read("(+) foo bar");
        Combination application = (Combination) prg.get(0);
        Symbol head = (Symbol) application.head();
        assertEquals("+", head.getName());
        assertEquals(3, application.size());
    }

    @Test
    public void combinationInList() throws IOException {
        Block prg = Reader.read("[a 1, b 2]");
        SList list = (SList) prg.get(0);
        assertThat(list.get(0), is(Combination.class));
        assertThat(list.get(1), is(Combination.class));
        Combination c1 = (Combination) list.get(0);
        assertThat(c1.head(), is(Symbol.class));
        assertEquals("a", ((Symbol) c1.head()).getName());
        assertThat(c1.get(1), is(SNumber.class));
        assertEquals(2, list.size());
    }

    @Test
    public void combinationInMap() throws IOException {
        Block prg = Reader.read("{a: 1 + 2, b 3: 4}");
        SMap map = (SMap) prg.get(0);
        assertTrue(map.containsKey(new Symbol("a")));
        assertThat(map.get(new Symbol("a")), is(Combination.class));
        assertEquals(2, map.keySet().size());
    }

    @Test
    public void infixRepeating() throws IOException {
        // All operators have the same priority and left associativity
        Block prg = Reader.read("foo + bar - spam");

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
        Block prg = Reader.read("foo (bar 42)");
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
        Block prg = Reader.read("foo:\n\tbar 42\nspam");
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
