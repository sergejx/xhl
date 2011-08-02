package xhl.core.elements;

import java.util.HashMap;

/**
 * @author Sergej Chodarev
 */
public class LMap extends Expression {
    private final HashMap<Object, Object> map = new HashMap<Object, Object>();

    public LMap(CodePosition position) {
        super(position);
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Object put(Object key, Object value) {
        return map.put(key, value);
    }



}
