package xhl.core.validator;

import java.util.Iterator;
import java.util.Map;

import xhl.core.elements.Symbol;

import static com.google.common.collect.Maps.newHashMap;

public class Schema implements Iterable<ElementSchema> {
    private final Map<Symbol, ElementSchema> elements =
            newHashMap();

    public boolean containsKey(Symbol sym) {
        return elements.containsKey(sym);
    }

    public ElementSchema get(Symbol sym) {
        return elements.get(sym);
    }

    public ElementSchema put(ElementSchema element) {
        return elements.put(element.getSymbol(), element);
    }

    @Override
    public Iterator<ElementSchema> iterator() {
        return elements.values().iterator();
    }
}
