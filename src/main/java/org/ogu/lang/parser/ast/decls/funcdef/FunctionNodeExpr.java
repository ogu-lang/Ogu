package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;

/**
 * An expression inside a function
 * Created by ediaz on 23-01-16.
 */
public class FunctionNodeExpr  extends FunctionNode {
    private Expression expression;


    public FunctionNodeExpr(Expression expression) {
        this.expression = expression;
        this.expression.setParent(this);
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expression);
    }
}
