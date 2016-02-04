package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * When a declaration is internal to a class o trait
 * Created by ediaz on 25-01-16.
 */
public abstract class FunctionalDeclaration extends ExportableDeclaration {

    private ContractDeclaration contract;

    protected FunctionalDeclaration(List<Decorator> decorators) {
        super(decorators);
    }


    public void setContract(ContractDeclaration contract) {
        this.contract = contract;
    }

    public ContractDeclaration getContract() {
        return this.contract;
    }

    protected FunctionalDeclaration(NameNode name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
