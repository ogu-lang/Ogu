package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * List by comprehension
 * [(a,b) | a <- set, b <- set]
 * Created by ediaz on 31-01-16.
 */
public class ListByComprehensionExpressionNode extends ListExpressionNode {


    ExpressionNode value;
    List<SetConstraintNode> constraints;

    public ListByComprehensionExpressionNode(ExpressionNode value, List<SetConstraintNode> constraints) {
        super();
        this.value = value;
        this.value.setParent(this);
        this.constraints = new ArrayList<>();
        this.constraints.addAll(constraints);
        this.constraints.forEach((c) -> c.setParent(this));
    }


    @Override
    public String toString() {
        return "ListByComprehension {" +
                "value = "+value +
                ", contraints = "+constraints+
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(value).addAll(constraints).build();
    }
}
