package sk.tuke.xhl.core.elements;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Sergej Chodarev
 */
public class SMap extends Expression {
    private final HashMap<Expression, Expression> map = new HashMap<>();

    public SMap(Position position) {
        super(position);
    }

    public boolean containsKey(Expression key) {
        return map.containsKey(key);
    }

    public Expression get(Expression key) {
        return map.get(key);
    }

    public Expression put(Expression key, Expression value) {
        return map.put(key, value);
    }

    public Set<Expression> keySet() {
        return map.keySet();
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
