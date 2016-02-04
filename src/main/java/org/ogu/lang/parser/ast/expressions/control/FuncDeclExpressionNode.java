package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.FunctionalDeclarationNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * func inside doBlock
 * Created by ediaz on 31-01-16.
 */
public class FuncDeclExpressionNode extends DeclExpressionNode {

    private FunctionalDeclarationNode funcDeclaration;

    public FuncDeclExpressionNode(FunctionalDeclarationNode funcDeclaration) {
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
