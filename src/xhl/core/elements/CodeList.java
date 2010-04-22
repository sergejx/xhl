package xhl.core.elements;

/**
 * List used to represent code.
 *
 * Technically the same thing as DataList, it just has different interpretation
 * during evaluation.
 *
 * @author Sergej Chodarev
 */
public class CodeList extends DataList {
    public CodeList(CodePosition position) {
        super(position);
    }
}
