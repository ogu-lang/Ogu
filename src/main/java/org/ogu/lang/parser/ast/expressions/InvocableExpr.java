package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class InvocableExpr extends ExpressionNode {

    protected List<ActualParam> actualParams;

    public List<ActualParam> getActualParams() {
        return actualParams;
    }

    public InvocableExpr(List<ActualParam> actualParams) {
        this.actualParams = new ArrayList<>();
        this.actualParams.addAll(actualParams);
        this.actualParams.forEach((p) -> p.setParent(this));
    }

    protected abstract List<? extends FormalParameter> formalParameters(SymbolResolver resolver);

}
