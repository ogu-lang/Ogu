package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.IdentifierNode;

import java.util.List;

/**
 * Exports a function
 * Created by ediaz on 22-01-16.
 */
public class ExportsFunctionDeclarationNode extends ExportsDeclarationNode {

    @Override
    public String toString() {
        return "ExportsFunctionDeclaration{" +
                "reference=" + name +
                ", decorators=" + decorators +
                '}';
    }

    public ExportsFunctionDeclarationNode(IdentifierNode referenceName, List<Decorator> decorators) {
        super(referenceName, decorators);
    }

}
