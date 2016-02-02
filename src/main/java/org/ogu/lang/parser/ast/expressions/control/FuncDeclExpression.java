package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.FunctionalDeclaration;
import org.ogu.lang.parser.ast.decls.LetDeclaration;
import org.ogu.lang.parser.ast.decls.ValDeclaration;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * func inside doBlock
 * Created by ediaz on 31-01-16.
 */
public class FuncDeclExpression extends DeclExpression {

    private FunctionalDeclaration funcDeclaration;

    public FuncDeclExpression(FunctionalDeclaration funcDeclaration) {
        super();
        this.funcDeclaration = funcDeclaration;
        this.funcDeclaration.setParent(this);
    }

    @Override
    public String toString() {
        return "FunctionalDeclaration { func="+funcDeclaration+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(funcDeclaration);
    }
}
