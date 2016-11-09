package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.jdk.ReflectionBasedSetOfOverloadedMethods;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A concrete functionc all
 * Created by ediaz on 21-01-16.
 */
public class FunctionCallNode extends InvocableExpressionNode {

    private ExpressionNode function;

    public ExpressionNode getFunction() {
        return function;
    }


    @Override
    public String toString() {

        return "FunctionCall{" +
                "function=" + function +
                ", actualParams=" + actualParamNodes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionCallNode that = (FunctionCallNode) o;

        if (!actualParamNodes.equals(that.actualParamNodes)) return false;
        if (!function.equals(that.function)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + actualParamNodes.hashCode();
        return result;
    }

    private ExpressionNode subject = null;
    public FunctionCallNode(ExpressionNode name, List<ActualParamNode> actualParamNodes) {
        super(actualParamNodes);
        this.function = name;
        this.function.setParent(this);
        if (actualParamNodes.size() > 0) {
            ActualParamNode param = actualParamNodes.get(0);
            subject = param.getValue();
        }
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(function).addAll(actualParamNodes).build();
    }

    @Override
    public TypeUsage calcType() {
        if (function.calcType().isInvocable()) {
            return function.calcType().asInvocable().internalInvocableDefinitionFor(actualParamNodes).get().asFunction().getReturnType();
        } else {
            return function.calcType();
        }
    }

    @Override
    public boolean isOnOverloaded(SymbolResolver resolver) {
        return function.calcType().asInvocable().isOverloaded();
    }


    @Override
    protected List<? extends FormalParameter> formalParameters(SymbolResolver resolver) {
        Logger.debug("function is "+function);
        return function.findFormalParametersFor(this).get();
    }

    public List<ExpressionNode> getActualParamValuesInOrder() {
        List<ExpressionNode> values = new LinkedList<>();
        for (ActualParamNode actualParam : actualParamNodes) {
            values.add(actualParam.getValue());
        }
        return values;
    }

    public boolean isStatic() {
        Symbol f = function;
        if (f instanceof ReferenceNode) {
            f = ((ReferenceNode) function).resolve(symbolResolver());
        }
        if (f instanceof ReflectionBasedSetOfOverloadedMethods) {
            return ((ReflectionBasedSetOfOverloadedMethods) f).isStatic();
        }
        throw new UnsupportedOperationException(f.getClass().getCanonicalName());
    }
}
