package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.TypeIdentifierNode;

import java.util.List;

/**
 * Exports a Type
 * Created by ediaz on 22-01-16.
 */
public class ExportsTypeDeclarationNode extends ExportsDeclarationNode {


    @Override
    public String toString() {
        return "ExportsTypeDeclaration{" +
                "type=" + name +
                ", decorators" + decorators +
                '}';
    }

    public ExportsTypeDeclarationNode(TypeIdentifierNode type, List<Decorator> decorators) {
        super(type, decorators);
    }


}
