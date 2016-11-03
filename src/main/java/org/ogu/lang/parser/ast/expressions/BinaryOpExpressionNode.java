package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * BinaryOps
 * Created by ediaz on 27-01-16.
 */
public class BinaryOpExpressionNode extends ExpressionNode {

    protected OperatorNode op;
    private ExpressionNode leftExpr;
    private ExpressionNode rightExpr;


    public BinaryOpExpressionNode(OperatorNode op, ExpressionNode left, ExpressionNode right) {
        super();
        this.op = op;
        this.op.setParent(this);
        this.leftExpr = left;
        this.leftExpr.setParent(this);
        this.rightExpr = right;
        this.rightExpr.setParent(this);
    }

    public ExpressionNode getLeft() {
        return leftExpr;
    }

    public ExpressionNode getRight() {
        return rightExpr;
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
        return leftExpr.calcType();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(op).add(leftExpr).add(rightExpr).build();
    }
}
