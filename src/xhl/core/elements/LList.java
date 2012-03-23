package xhl.core.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * List
 *
 * @author Sergej Chodarev
 */
public class LList extends Expression implements Iterable<Expression> {

    private final List<Expression> list = new LinkedList<Expression>();

    public LList(CodePosition position) {
        super(position);
    }

    private LList(List<Expression> l) {
        list.addAll(l);
    }

    public boolean add(Expression e) {
        return list.add(e);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<Expression> iterator() {
        return list.iterator();
    }

    public Expression head() {
        return list.get(0);
    }

    public LList tail() {
        return new LList(list.subList(1, list.size()));
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
