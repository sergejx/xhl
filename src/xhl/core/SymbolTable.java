package xhl.core;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<Symbol, Object> table = new HashMap<Symbol, Object>();

    public boolean containsKey(Symbol sym) {
        return table.containsKey(sym);
    }

    public Object get(Symbol sym) {
        return table.get(sym);
    }

    public Object put(Symbol sym, Object value) {
        return table.put(sym, value);
    }

    public void putAll(SymbolTable t) {
        for (Symbol key : t.table.keySet()) {
            put(key, t.get(key));
        }
    }

}
