package xhl.core.validator;

import xhl.core.elements.Symbol;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Element declaration.
 */
public class ElementSchema {
    private final Symbol symbol;
    private List<ParamSpec> params = newArrayList();
    private Type type = Type.Null;
    private final List<DefSpec> defines = newArrayList();

    public ElementSchema(Symbol sym) {
        this.symbol = sym;
    }

    public ElementValidator getValidator() {
        return new SchemaElementValidator(this);
    }

    /**
     * Get symbol representing the element.
     * @return Element symbol
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Get properties of the element parameters.
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
     * @return Definition specifications
     */
    public List<DefSpec> getDefines() {
        return defines;
    }

    public void addDefine(DefSpec spec) {
        defines.add(spec);
    }

    /**
     * Check if the element has variable parameters list.
     * @return <code>true</code> if the element is variadic
     */
    public boolean isVariadic() {
        return params.get(params.size()-1).isVariadic();
    }

    public static enum PassingMethod { VAL, SYM }

    /**
     * Element parameter specification
     */
    public static class ParamSpec {
        private final Type type;
        private final PassingMethod method;
        private final boolean variadic;
        private final boolean block;

        /** Declare parameter received by value. */
        public static ParamSpec val(Type type) {
            return new ParamSpec(PassingMethod.VAL, type, false, false);
        }

        /** Declare parameter received symbolically. */
        public static ParamSpec sym(Type type) {
            return new ParamSpec(PassingMethod.SYM, type, false, false);
        }

        /** Mark the parameter as variadic.
         * @return A new parameter specification with the same type
         */
        public static ParamSpec variadic(ParamSpec spec) {
            return new ParamSpec(spec.getMethod(), spec.getType(), true, false);
        }

        /** Mark the parameter as a block parameter.
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

        /** Get parameter passing method */
        public PassingMethod getMethod() {
            return method;
        }

        /** Get a type of the parameter */
        public Type getType() {
            return type;
        }

        /** Is the parameter variadic? */
        public boolean isVariadic() {
            return variadic;
        }

        /** Does the parameter expect a block */
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

        public DefSpec(int arg, Type type) {
            this(arg, type, false);
        }

        public DefSpec(int arg, Type type, boolean forward) {
            this.arg = arg;
            this.type = type;
            this.backward = forward;
        }

        /** Symbol type */
        public Type getType() {
            return type;
        }

        /** Index of an element argument that contains defined symbol. */
        public int getArg() {
            return arg;
        }

        /** Is the symbol defined backwards? */
        public boolean isBackward() {
            return backward;
        }
    }
}
