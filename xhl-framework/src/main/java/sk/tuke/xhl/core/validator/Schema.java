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
package sk.tuke.xhl.core.validator;

import sk.tuke.xhl.core.elements.Symbol;

import java.util.*;

public class Schema implements Iterable<ElementSchema> {
    private final Map<Symbol, ElementSchema> elements = new HashMap<>();
    private final List<Import> imports = new ArrayList<>();

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
