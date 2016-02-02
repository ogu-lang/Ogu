package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Ifs
 * Created by ediaz on 29-01-16.
 */
public class IfExpression extends Expression {

    private Expression cond;
    private Expression thenPart;
    private Expression elsePart;

    public IfExpression(Expression cond, Expression thenPart, Expression elsePart) {
        super();
        this.cond = cond;
        this.cond.setParent(this);
        this.thenPart = thenPart;
        this.thenPart.setParent(this);
        this.elsePart = elsePart;
        this.elsePart.setParent(this);
    }



    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(cond).add(thenPart).add(elsePart).build();
    }


    @Override
    public String toString() {
        return "If{" +
                "cond=" + cond +
                ", then=" + thenPart +
                ", else=" + elsePart +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }
}
