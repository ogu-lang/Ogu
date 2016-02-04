package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

abstract public class ExportsDeclarationNode extends NameDeclarationNode {


    protected ExportsDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }
}