package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Created by ediaz on 25-01-16.
 */
public abstract class TypeDeclarationNode extends ExportableDeclarationNode {

    protected TypeDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }
}
