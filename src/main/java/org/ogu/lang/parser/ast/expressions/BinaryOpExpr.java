package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguOperator;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Bi
 * Created by ediaz on 27-01-16.
 */
public class BinaryOpExpr extends Expression {

    OguOperator op;
    Expression leftExpr;
    Expression rightExpr;


    public BinaryOpExpr(OguOperator op, Expression left, Expression right) {
        super();
        this.op = op;
        this.op.setParent(this);
        this.leftExpr = left;
        this.leftExpr.setParent(this);
        this.rightExpr = right;
        this.rightExpr.setParent(this);
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(op).add(leftExpr).add(rightExpr).build();
    }
}
