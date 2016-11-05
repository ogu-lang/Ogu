package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * An expression inside a function
 * Created by ediaz on 23-01-16.
 */
public class FunctionNodeExpr  extends FunctionNode {
    private ExpressionNode expressionNode;


    public FunctionNodeExpr(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
        this.expressionNode.setParent(this);
    }


    public ExpressionNode getExpression() {
        return expressionNode;
    }

    @Override
    public TypeUsage calcType() {
        return expressionNode.calcType();
    }


    @Override
    public String toString() {
        return expressionNode.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expressionNode);
    }
}
