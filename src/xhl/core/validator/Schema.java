package xhl.core.validator;

import xhl.core.elements.Symbol;

import java.util.*;

public class Schema implements Iterable<ElementSchema> {
    private final Map<Symbol, ElementSchema> elements = new HashMap<>();
    private List<Import> imports = new ArrayList<>();

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

    public List<Import> getImports() {
        return imports;
    }

    public void addImport(Import moduleName) {
        imports.add(moduleName);
    }

    public static class Import implements Iterable<Symbol> {
        private final String module;
        private final List<Symbol> elements;
        private final boolean allElements;

        /**
         * Define new import declaration selecting several elements from the
         * module.
         *
         * @param module   A name of the module.
         * @param elements A list of imported elements.
         */
        public Import(String module, List<Symbol> elements) {
            this.module = module;
            this.elements = elements;
            allElements = false;
        }

        /**
         * Define new import declaration selecting <strong>all elements</strong>
         * from the module.
         *
         * @param module A name of the module.
         */
        public Import(String module) {
            this.module = module;
            elements = Collections.emptyList();
            allElements = true;
        }

        public String getModule() {
            return module;
        }

        /**
         * Are implemented all modules?
         */
        public boolean allElements() {
            return allElements;
        }

        /**
         * Iterate over imported elements. If <code>allElements()</code> is
         * <code>true</code>, this list is empty.
         *
         * @return Iterator over the names of elements.
         */
        @Override
        public Iterator<Symbol> iterator() {
            return elements.iterator();
        }
    }
}
