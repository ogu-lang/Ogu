package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Declares an alias
 * Created by ediaz on 22-01-16.
 */
public abstract class AliasDeclaration extends NameDeclaration {

    protected AliasDeclaration(List<Decorator> decorators) { super(decorators); }
    protected AliasDeclaration(NameNode id, List<Decorator> decorators) {
        super(id, decorators);
    }
}
