package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.control.DoExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * \args -> expr
 * Created by ediaz on 01-02-16.
 */
public class LambdaExpressionNode extends ExpressionNode {

    private List<LambdaArgNode> args;
    private DoExpressionNode doExpr;

    public LambdaExpressionNode(List<LambdaArgNode> args, DoExpressionNode expr) {
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.doExpr = expr;
        this.doExpr.setParent(this);
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).add(doExpr).build();
    }


}
