package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguName;

import java.util.List;

/**
 * Declares an alias
 * Created by ediaz on 22-01-16.
 */
public abstract class AliasDeclaration extends NameDeclaration {

    protected AliasDeclaration(List<Decorator> decorators) { super(decorators); }
    protected AliasDeclaration(OguName id, List<Decorator> decorators) {
        super(id, decorators);
    }
}
