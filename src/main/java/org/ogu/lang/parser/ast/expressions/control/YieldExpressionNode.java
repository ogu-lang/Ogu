package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * yield expr
 * Created by ediaz on 01-02-16.
 */
public class YieldExpressionNode extends ExpressionNode {

    private ExpressionNode expressionNode;

    public YieldExpressionNode(ExpressionNode expressionNode) {
        super();
        this.expressionNode = expressionNode;
        this.expressionNode.setParent(this);
    }


    @Override
    public String toString() {
        return "Yield{"+ expressionNode +'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expressionNode);
    }
}
