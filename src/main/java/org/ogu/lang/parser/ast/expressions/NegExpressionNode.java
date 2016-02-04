package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * (- expr) or - 10
 * Created by ediaz on 30-01-16.
 */
public class NegExpressionNode extends ExpressionNode {

    private ExpressionNode expr;

    public NegExpressionNode(ExpressionNode expr) {
        this.expr = expr;
        this.expr.setParent(this);
    }


    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expr);
    }

    @Override
    public String toString() {
        return "(-) {" + expr + '}';
    }
}
