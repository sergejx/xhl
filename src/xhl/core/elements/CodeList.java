package xhl.core.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * List used to represent code
 *
 * @author Sergej Chodarev
 */
public class CodeList extends CodeElement implements Iterable<Object> {
    private final List<Object> list = new LinkedList<Object>();

    public CodeList(CodePosition position) {
        super(position);
    }

    private CodeList(List<Object> l) {
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

    public CodeList tail() {
        return new CodeList(list.subList(1, list.size()));
    }
}
