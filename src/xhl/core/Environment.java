package xhl.core;

import xhl.core.elements.Symbol;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

public class Environment<T> {
    private Map<Symbol, T> table;
    private final Deque<Map<Symbol, T>> stack = newLinkedList();

    public Environment() {
        push(); // Global namespace
    }

    public boolean containsKey(Symbol sym) {
        for (Map<Symbol, T> tbl : stack)
            if (tbl.containsKey(sym))
                return true;
        return false;
    }

    public T get(Symbol sym) {
        for (Map<Symbol, T> tbl : stack)
            if (tbl.containsKey(sym))
                return tbl.get(sym);
        return null;
    }

    public T put(Symbol sym, T value) {
        return table.put(sym, value);
    }

    public T putGlobal(Symbol sym, T value) {
        return stack.getLast().put(sym, value);
    }

    public void putAll(Environment<T> t) {
        for (Symbol key : t.table.keySet()) {
            put(key, t.get(key));
        }
    }

    public void putAll(Map<Symbol, T> t) {
        for (Symbol key : t.keySet()) {
            put(key, t.get(key));
        }
    }

    public void putAllGlobal(Map<Symbol, T> t) {
        for (Symbol key : t.keySet()) {
            putGlobal(key, t.get(key));
        }
    }


    public Set<Symbol> keySet() {
        Set<Symbol> keys = newHashSet();
        for (Map<Symbol, T> tbl : stack)
            keys.addAll(tbl.keySet());
        return keys;
    }

    /**
     * Push a new local environment on the stack.
     */
    public void push() {
        table = newHashMap();
        stack.push(table);
    }

    /**
     * Remove local environment.
     */
    public void pop() {
        stack.removeFirst();
        table = stack.peek();
    }
}
