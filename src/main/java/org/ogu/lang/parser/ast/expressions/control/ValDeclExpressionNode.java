package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.ValDeclarationNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * val inside doBlock
 * Created by ediaz on 31-01-16.
 */
public class ValDeclExpressionNode extends DeclExpressionNode {

    private ValDeclarationNode valDeclaration;

    public ValDeclExpressionNode(ValDeclarationNode valDeclaration) {
        super();
        this.valDeclaration = valDeclaration;
        this.valDeclaration.setParent(this);
    }

    @Override
    public String toString() {
        return "ValDeclExpression { val="+valDeclaration+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(valDeclaration);
    }
}
