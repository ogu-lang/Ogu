package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * All exportable declarations: Types, Funcs, Vals and Vars
 * Created by ediaz on 24-01-16.
 */
public abstract class ExportableDeclaration extends NameDeclaration {

    protected ExportableDeclaration(List<Decorator> decorators) {
        super(decorators);
    }


    protected ExportableDeclaration(NameNode name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
