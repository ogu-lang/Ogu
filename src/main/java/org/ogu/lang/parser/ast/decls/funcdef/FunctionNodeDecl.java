package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.FunctionalDeclarationNode;

/**
 * An expression inside a function
 * Created by ediaz on 23-01-16.
 */
public class FunctionNodeDecl extends FunctionNode {
    private FunctionalDeclarationNode declaration;


    public FunctionNodeDecl(FunctionalDeclarationNode declaration) {
        this.declaration = declaration;
        this.declaration.setParent(this);
    }

    @Override
    public String toString() {
        return declaration.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(declaration);
    }
}
