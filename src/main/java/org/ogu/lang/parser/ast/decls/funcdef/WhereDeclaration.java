package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.LetDeclarationNode;
import org.ogu.lang.parser.ast.decls.TupleValDeclarationNode;

/**
 * where ....
 * Created by ediaz on 01-02-16.
 */
public class WhereDeclaration extends FunctionNode {

    private TupleValDeclarationNode tupleDeclaration;
    private LetDeclarationNode letDeclaration;

    public WhereDeclaration(TupleValDeclarationNode tupleDeclaration) {
        this.tupleDeclaration = tupleDeclaration;
    }

    public WhereDeclaration(LetDeclarationNode letDeclaration) {
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