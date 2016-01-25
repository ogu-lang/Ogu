package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguName;

import java.util.List;

/**
 * A Contract is a declarations that contains methods
 * class, trait and instance are contracts
 * Created by ediaz on 25-01-16.
 */
public class ContractDeclaration extends ExportableDeclaration {


    protected ContractDeclaration(OguName name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
