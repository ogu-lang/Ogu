package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Declares an alias
 * Created by ediaz on 22-01-16.
 */
public abstract class AliasDeclarationNode extends NameDeclarationNode  {

    protected AliasDeclarationNode(List<DecoratorNode> decoratorNodes) { super(decoratorNodes); }
    protected AliasDeclarationNode(NameNode id, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
    }
}
