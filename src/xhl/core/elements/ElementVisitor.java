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
    public R visit(SNumber num);
    public R visit(SBoolean bool);
    public R visit(SString str);
    public R visit(SList lst);
    public R visit(SMap map);
    public R visit(Symbol sym);
    public R visit(Combination cmb);
    public R visit(Block blk);
}
