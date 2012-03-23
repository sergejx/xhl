package xhl.core.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * List
 *
 * @author Sergej Chodarev
 */
public class LList extends Expression implements Iterable<Object> {

    private final List<Object> list = new LinkedList<Object>();

    public LList(CodePosition position) {
        super(position);
    }

    private LList(List<Object> l) {
        list.addAll(l);
    }

    public boolean add(Object e) {
        return list.add(e);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    public Object head() {
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
