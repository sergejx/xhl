package xhl.core.elements;

/**
 * Visitor for elements of the language.
 *
 * Implement this if you want to process a tree of language elements.
 *
 * @author Sergej Chodarev
 *
 * @param <R>
 *            Type of the result
 */
public interface ElementVisitor<R> {
    public R visit(LNumber num);
    public R visit(LBoolean bool);
    public R visit(LString str);
    public R visit(LList lst);
    public R visit(LMap map);
    public R visit(Symbol sym);
    public R visit(Combination cmb);
    public R visit(Block blk);
}
