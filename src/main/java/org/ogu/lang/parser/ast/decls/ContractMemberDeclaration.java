package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;

import java.util.List;

/**
 * When a declaration is internal to a class o trait
 * Created by ediaz on 25-01-16.
 */
public abstract class ContractMemberDeclaration extends ExportableDeclaration {

    protected ContractMemberDeclaration(OguName name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
