package xhl.core.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for composed elements, that are lists of expressions.
 *
 * @author Sergej Chodarev
 */
public abstract class ExpressionsList extends Expression {

    protected final List<Expression> list = new LinkedList<Expression>();

    protected ExpressionsList() {
    }

    public ExpressionsList(Position position) {
        super(position);
    }

    public boolean add(Expression e) {
        return list.add(e);
    }

    public int size() {
        return list.size();
    }

    public Expression get(int arg0) {
        return list.get(arg0);
    }

    public List<Expression> subList(int from, int to) {
        return list.subList(from, to);
    }

    public Iterator<Expression> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}