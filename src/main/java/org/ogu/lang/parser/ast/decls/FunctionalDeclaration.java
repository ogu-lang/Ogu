package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.expressions.Expression;

import java.util.List;

/**
 * When a declaration is internal to a class o trait
 * Created by ediaz on 25-01-16.
 */
public abstract class FunctionalDeclaration extends ExportableDeclaration {

    private ContractDeclaration contract;


    public void setContract(ContractDeclaration contract) {
        this.contract = contract;
    }

    public ContractDeclaration getContract() {
        return this.contract;
    }

    protected FunctionalDeclaration(OguName name, List<Decorator> decorators) {
        super(name, decorators);
    }
}
