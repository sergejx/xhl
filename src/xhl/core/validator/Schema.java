package xhl.core.validator;

import xhl.core.elements.Symbol;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class Schema implements Iterable<ElementSchema> {
    private final Map<Symbol, ElementSchema> elements =
            newHashMap();
    private List<String> imports = newArrayList();

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

    public List<String> getImports() {
        return imports;
    }

    public void addImport(String moduleName) {
        imports.add(moduleName);
    }
}
