package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * All exportable declarations: Types, Funcs, Vals and Vars
 * Created by ediaz on 24-01-16.
 */
public abstract class ExportableDeclarationNode extends NameDeclarationNode {

    protected ExportableDeclarationNode(List<Decorator> decorators) {
        super(decorators);
    }


    protected ExportableDeclarationNode(NameNode name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
