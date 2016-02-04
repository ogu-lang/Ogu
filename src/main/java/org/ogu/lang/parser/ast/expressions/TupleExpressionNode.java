package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * (expr,expr,...)
 * Created by ediaz on 31-01-16.
 */
public class TupleExpressionNode extends ExpressionNode {

    List<ExpressionNode> exprs;

    public TupleExpressionNode(List<ExpressionNode> exprs) {
        super();
        this.exprs = new ArrayList<>();
        this.exprs.addAll(exprs);
        this.exprs.forEach((e) -> e.setParent(this));
    }

    @Override
    public String toString() {
        return "Tuple { exprs = "+exprs+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(exprs).build();
    }

}
