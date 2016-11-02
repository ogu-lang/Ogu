package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.InvocableDefinition;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * When a declaration is internal to a class o trait
 * Created by ediaz on 25-01-16.
 */
public abstract class FunctionalDeclarationNode extends ExportableDeclarationNode implements InvocableDefinition {

    private ContractDeclarationNode contract;

    protected FunctionalDeclarationNode(List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
    }


    public void setContract(ContractDeclarationNode contract) {
        this.contract = contract;
    }

    public ContractDeclarationNode getContract() {
        return this.contract;
    }

    protected FunctionalDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }

    @Override
    public TypeUsage getReturnType() {
        return null;
    }

    @Override
    public List<? extends FormalParameter> getParameters() {
        return null;
    }


}
