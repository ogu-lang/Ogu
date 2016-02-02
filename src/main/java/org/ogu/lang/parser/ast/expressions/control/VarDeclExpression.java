package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.ValDeclaration;
import org.ogu.lang.parser.ast.decls.VarDeclaration;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * val inside doBlock
 * Created by ediaz on 31-01-16.
 */
public class VarDeclExpression extends DeclExpression {

    private VarDeclaration varDeclaration;

    public VarDeclExpression(VarDeclaration varDeclaration) {
        super();
        this.varDeclaration = varDeclaration;
        this.varDeclaration.setParent(this);
    }

    @Override
    public String toString() {
        return "VarDeclExpression { var="+varDeclaration+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(varDeclaration);
    }
}
