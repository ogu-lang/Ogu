package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * yield expr
 * Created by ediaz on 01-02-16.
 */
public class YieldExpression extends Expression {

    private Expression expression;

    public YieldExpression(Expression expression) {
        super();
        this.expression = expression;
        this.expression.setParent(this);
    }


    @Override
    public String toString() {
        return "Yield{"+expression+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expression);
    }
}
