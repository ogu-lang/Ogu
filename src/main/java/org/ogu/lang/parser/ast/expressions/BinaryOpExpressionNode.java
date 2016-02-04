package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Bi
 * Created by ediaz on 27-01-16.
 */
public class BinaryOpExpressionNode extends ExpressionNode {

    OperatorNode op;
    ExpressionNode leftExpr;
    ExpressionNode rightExpr;


    public BinaryOpExpressionNode(OperatorNode op, ExpressionNode left, ExpressionNode right) {
        super();
        this.op = op;
        this.op.setParent(this);
        this.leftExpr = left;
        this.leftExpr.setParent(this);
        this.rightExpr = right;
        this.rightExpr.setParent(this);
    }

    @Override
    public String toString() {
        return "BinaryOp {"+
                "op='"+op+"'"+
                ", left="+leftExpr+
                ", right="+rightExpr+
                '}';
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
