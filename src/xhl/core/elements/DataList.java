package xhl.core.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * List
 *
 * @author Sergej Chodarev
 */
public class DataList extends Expression implements Iterable<Object> {

    private final List<Object> list = new LinkedList<Object>();

    public DataList(CodePosition position) {
        super(position);
    }

    private DataList(List<Object> l) {
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

    public DataList tail() {
        return new DataList(list.subList(1, list.size()));
    }
}
