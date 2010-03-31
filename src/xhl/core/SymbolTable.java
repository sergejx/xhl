package xhl.core;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Object> table = new HashMap<String, Object>();

    public boolean containsKey(Symbol sym) {
        return table.containsKey(sym.getName());
    }

    public Object get(Symbol sym) {
        return table.get(sym.getName());
    }

    public Object put(Symbol sym, Object value) {
        return table.put(sym.getName(), value);
    }

    public void putAll(SymbolTable t) {
        for (String key : t.table.keySet()) {
            table.put(key, t.table.get(key));
        }
    }

}
