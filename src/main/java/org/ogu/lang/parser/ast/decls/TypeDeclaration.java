package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Created by ediaz on 25-01-16.
 */
public abstract class TypeDeclaration  extends ExportableDeclaration {

    protected TypeDeclaration(NameNode name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
