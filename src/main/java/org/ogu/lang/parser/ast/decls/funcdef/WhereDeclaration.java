package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.LetDeclaration;
import org.ogu.lang.parser.ast.decls.TupleValDeclaration;

/**
 * where ....
 * Created by ediaz on 01-02-16.
 */
public class WhereDeclaration extends FunctionNode {

    private TupleValDeclaration tupleDeclaration;
    private LetDeclaration letDeclaration;

    public WhereDeclaration(TupleValDeclaration tupleDeclaration) {
        this.tupleDeclaration = tupleDeclaration;
    }

    public WhereDeclaration(LetDeclaration letDeclaration) {
        this.letDeclaration = letDeclaration;
    }

    @Override
    public String toString() {
        if (tupleDeclaration != null)
            return "Where {" + tupleDeclaration + '}';
        else
            return "Where {" + letDeclaration + '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        if (tupleDeclaration != null)
            return ImmutableList.of(tupleDeclaration);
        else
            return ImmutableList.of(letDeclaration);
    }
}