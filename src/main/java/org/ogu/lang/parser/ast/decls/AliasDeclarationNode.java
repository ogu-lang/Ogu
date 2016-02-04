package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Declares an alias
 * Created by ediaz on 22-01-16.
 */
public abstract class AliasDeclarationNode extends NameDeclarationNode {

    protected AliasDeclarationNode(List<Decorator> decorators) { super(decorators); }
    protected AliasDeclarationNode(NameNode id, List<Decorator> decorators) {
        super(id, decorators);
    }
}
