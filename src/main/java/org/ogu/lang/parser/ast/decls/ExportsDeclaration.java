package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

abstract public class ExportsDeclaration extends NameDeclaration {


    protected ExportsDeclaration(NameNode name, List<Decorator> decorators) {
        super(name, decorators);
    }
}