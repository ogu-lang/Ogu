package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.OguName;

import java.util.List;

/**
 * All exportable declarations: Types, Funcs, Vals and Vars
 * Created by ediaz on 24-01-16.
 */
public abstract class ExportableDeclaration extends NameDeclaration {

    protected ExportableDeclaration(OguName name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
