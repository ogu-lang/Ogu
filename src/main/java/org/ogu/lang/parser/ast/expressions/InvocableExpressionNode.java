package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class InvocableExpressionNode extends ExpressionNode {

    protected List<ActualParamNode> actualParamNodes;

    public List<ActualParamNode> getActualParamNodes() {
        return actualParamNodes;
    }

    public InvocableExpressionNode(List<ActualParamNode> actualParamNodes) {
        this.actualParamNodes = new ArrayList<>();
        this.actualParamNodes.addAll(actualParamNodes);
        this.actualParamNodes.forEach((p) -> p.setParent(this));
    }

    protected abstract List<? extends FormalParameter> formalParameters(SymbolResolver resolver);

}
