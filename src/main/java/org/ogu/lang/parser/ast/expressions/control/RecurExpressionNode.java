package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Useful for tail recursion
 * recur expr*
 * Created by ediaz on 01-02-16.
 */
public class RecurExpressionNode extends ExpressionNode {

    private List<ExpressionNode> args;

    public RecurExpressionNode(List<ExpressionNode> args) {
        super();
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
    }

    @Override
    public String toString() {
        return "Recur{"+args+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).build();
    }
}
