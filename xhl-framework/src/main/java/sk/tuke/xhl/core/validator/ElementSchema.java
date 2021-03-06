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

import java.util.ArrayList;
import java.util.List;

/**
 * Element declaration.
 */
public class ElementSchema {
    private final Symbol symbol;
    private String doc;
    private List<ParamSpec> params = new ArrayList<>();
    private Type type = Type.Null;
    private final List<DefSpec> defines = new ArrayList<>();
    private ElementValidator validator;
    private final List<ElementSchema> localElements = new ArrayList<>();

    public ElementSchema(Symbol sym) {
        this.symbol = sym;
        validator = new SchemaElementValidator(this);
    }

    public ElementValidator getValidator() {
        return validator;
    }

    public void setValidator(ElementValidator validator) {
        this.validator = validator;
    }

    /**
     * Get symbol representing the element.
     *
     * @return Element symbol
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Get element documentation
     */
    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    /**
     * Get properties of the element parameters.
     *
     * @return List of parameter specifications
     */
    public List<ParamSpec> getParams() {
        return params;
    }

    public void setParams(List<ParamSpec> params) {
        this.params = params;
    }

    /**
     * Get type of the result of element application.
     *
     * @return Element type
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get a list of
     *
     * @return Definition specifications
     */
    public List<DefSpec> getDefines() {
        return defines;
    }

    public void addDefine(DefSpec spec) {
        defines.add(spec);
    }

    public void addLocalElement(ElementSchema element) {
        localElements.add(element);
    }

    public List<ElementSchema> getLocalElements() {
        return localElements;
    }

    /**
     * Check if the element has variable parameters list.
     *
     * @return <code>true</code> if the element is variadic
     */
    public boolean isVariadic() {
        return params.get(params.size() - 1).isVariadic();
    }

    public static enum PassingMethod {VAL, SYM}

    /**
     * Element parameter specification
     */
    public static class ParamSpec {
        private final Type type;
        private final PassingMethod method;
        private final boolean variadic;
        private final boolean block;

        /**
         * Declare parameter received by value.
         */
        public static ParamSpec val(Type type) {
            return new ParamSpec(PassingMethod.VAL, type, false, false);
        }

        /**
         * Declare parameter received symbolically.
         */
        public static ParamSpec sym(Type type) {
            return new ParamSpec(PassingMethod.SYM, type, false, false);
        }

        /**
         * Mark the parameter as variadic.
         *
         * @return A new parameter specification with the same type
         */
        public static ParamSpec variadic(ParamSpec spec) {
            return new ParamSpec(spec.getMethod(), spec.getType(), true, false);
        }

        /**
         * Mark the parameter as a block parameter.
         *
         * @return A new parameter specification with the same type
         */
        public static ParamSpec block(ParamSpec spec) {
            return new ParamSpec(spec.getMethod(), spec.getType(), false, true);
        }

        private ParamSpec(PassingMethod method, Type type, boolean variadic,
                          boolean block) {
            this.method = method;
            this.type = type;
            this.variadic = variadic;
            this.block = block;
        }

        /**
         * Get parameter passing method
         */
        public PassingMethod getMethod() {
            return method;
        }

        /**
         * Get a type of the parameter
         */
        public Type getType() {
            return type;
        }

        /**
         * Is the parameter variadic?
         */
        public boolean isVariadic() {
            return variadic;
        }

        /**
         * Does the parameter expect a block
         */
        public boolean isBlock() {
            return block;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ParamSpec))
                return false;
            ParamSpec that = (ParamSpec) obj;
            return this.type.equals(that.type)
                    && this.method.equals(that.method);
        }
    }

    /**
     * Specification of a symbol defined by the element.
     */
    public static class DefSpec {
        private final Type type;
        private final int arg;
        private final boolean backward;
        private final boolean global;

        public DefSpec(int arg, Type type) {
            this(arg, type, false, false);
        }

        public DefSpec(int arg, Type type, boolean backward) {
            this(arg, type, backward, false);
        }

        private DefSpec(int arg, Type type, boolean backward, boolean global) {
            this.type = type;
            this.arg = arg;
            this.backward = backward;
            this.global = global;
        }

        public static DefSpec global(DefSpec spec) {
            return new DefSpec(spec.arg, spec.type, false, true);
        }

        /**
         * Symbol type
         */
        public Type getType() {
            return type;
        }

        /**
         * Index of an element argument that contains defined symbol.
         */
        public int getArg() {
            return arg;
        }

        /**
         * Is the symbol defined backwards?
         */
        public boolean isBackward() {
            return backward;
        }

        public boolean isGlobal() {
            return global;
        }
    }
}
