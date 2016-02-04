package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;
import java.util.List;

/**
 * Constructor Call (new T(expr))
 * Created by ediaz on 23-01-16.
 */
public class ConstructorNode extends InvocableExpressionNode {

    protected TypeReferenceNode type;

    public ConstructorNode(TypeReferenceNode type, List<ActualParam> params) {
        super(params);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        return "Constructor {"+
                "type = "+type +
                "params = "+actualParams +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(type).addAll(actualParams).build();
    }

    @Override
    protected List<? extends FormalParameter> formalParameters(SymbolResolver resolver) {
        return null;
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }
}
