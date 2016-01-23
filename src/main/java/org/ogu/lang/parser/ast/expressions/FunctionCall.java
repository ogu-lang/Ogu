package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * A concrete functionc all
 * Created by ediaz on 21-01-16.
 */
public class FunctionCall extends InvocableExpr  {

    private Expression function;

    public Expression getFunction() {
        return function;
    }

    @Override
    public String toString() {

        return "FunctionCall{" +
                "function='" + function + '\'' +
                ", actualParams=" + actualParams +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionCall that = (FunctionCall) o;

        if (!actualParams.equals(that.actualParams)) return false;
        if (!function.equals(that.function)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + actualParams.hashCode();
        return result;
    }

    public FunctionCall(Expression name, List<ActualParam> actualParams) {
        super(actualParams);
        this.function = name;
        this.function.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(function).addAll(actualParams).build();
    }

    @Override
    public TypeUsage calcType() {
        return function.calcType().asInvocable().internalInvocableDefinitionFor(actualParams).get().asFunction().getReturnType();
    }


    @Override
    protected List<? extends FormalParameter> formalParameters(SymbolResolver resolver) {
        return function.findFormalParametersFor(this).get();
    }


}
