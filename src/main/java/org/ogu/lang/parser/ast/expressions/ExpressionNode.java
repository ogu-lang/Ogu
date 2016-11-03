package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * An Expression
 * Created by ediaz on 21-01-16.
 */
public abstract class ExpressionNode extends Node {

    public abstract TypeUsage calcType();

    public JvmMethodDefinition findFunctionFor(List<ActualParamNode> argsTypes, SymbolResolver resolver) {
        throw new UnsupportedOperationException("On " + this.getClass().getCanonicalName());
    }


    public boolean isType(SymbolResolver resolver) {
        return false;
    }
}
