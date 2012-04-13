package xhl.core;

import java.util.Map;
import java.util.Set;

import xhl.core.elements.Symbol;

import static com.google.common.collect.Maps.newHashMap;

public class SymbolTable<T> {
    private final Map<Symbol, T> table = newHashMap();
    private SymbolTable<T> parent;

    public SymbolTable() {
    }

    public SymbolTable(SymbolTable<T> parent) {
        this.parent = parent;
    }

    public boolean containsKey(Symbol sym) {
        return table.containsKey(sym)
                || ((parent != null) && parent.containsKey(sym));
    }

    public T get(Symbol sym) {
        if (table.containsKey(sym))
            return table.get(sym);
        else if (parent != null)
            return parent.get(sym);
        else
            return null;
    }

    public T put(Symbol sym, T value) {
        return table.put(sym, value);
    }

    public void putAll(SymbolTable<T> t) {
        for (Symbol key : t.table.keySet()) {
            put(key, t.get(key));
        }
    }

    public Set<Symbol> keySet() {
        return table.keySet();
    }
}
