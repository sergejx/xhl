/* XHL - Extensible Host Language
 * Copyright 2012 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.tuke.xhl.core;

import sk.tuke.xhl.core.elements.Symbol;

import java.util.*;

public class Environment<T> {
    private Map<Symbol, T> table;
    private final Deque<Map<Symbol, T>> stack = new LinkedList<>();

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
        Set<Symbol> keys = new HashSet<>();
        for (Map<Symbol, T> tbl : stack)
            keys.addAll(tbl.keySet());
        return keys;
    }

    /**
     * Push a new local environment on the stack.
     */
    public void push() {
        table = new HashMap<>();
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
