package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncExprParamNode extends FunctionPatternParamNode {

    private ExpressionNode expr;


    public FuncExprParamNode(ExpressionNode expr) {
        super();
        this.expr = expr;
        this.expr.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncExprParam{"+
                "expr="+expr+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expr);
    }
}
