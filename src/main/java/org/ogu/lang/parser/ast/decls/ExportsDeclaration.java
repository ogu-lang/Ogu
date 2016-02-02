package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;

import java.util.List;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

abstract public class ExportsDeclaration extends NameDeclaration {


    protected ExportsDeclaration(OguName name, List<Decorator> decorators) {
        super(name, decorators);
    }
}