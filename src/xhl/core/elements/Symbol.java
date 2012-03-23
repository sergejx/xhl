package xhl.core.elements;

public class Symbol extends Expression {
    private final String name;

    public Symbol(String name) {
        this(name, null);
    }

    public Symbol(String name, CodePosition position) {
        super(position);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol sym = (Symbol) obj;
            return sym.name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
