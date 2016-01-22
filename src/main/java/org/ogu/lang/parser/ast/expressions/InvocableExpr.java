package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class InvocableExpr extends Expression {

    protected List<ActualParam> actualParams;
    protected List<ActualParam> originalParams;

    public List<ActualParam> getActualParams() {
        return actualParams;
    }

    public InvocableExpr(List<ActualParam> actualParams) {
        this.actualParams = new ArrayList<>();
        this.actualParams.addAll(actualParams);
        this.actualParams.forEach((p) -> p.setParent(this));
        originalParams = actualParams;
    }

    protected abstract List<? extends FormalParameter> formalParameters(SymbolResolver resolver);

}
