package xhl.core.elements;

import java.util.List;

/**
 * @author Sergej Chodarev
 */
public class Block extends Statement {
    private Expression head;
    private List<Statement> body;

    public Block(Expression head, List<Statement> body, CodePosition position) {
        super(position);
        this.head = head;
        this.body = body;
    }

    public Expression getHead() {
        return head;
    }

    public List<Statement> getBody() {
        return body;
    }
}
