package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Assign field values
 * {name="john", age=30}
 * Created by ediaz on 01-02-16.
 */
public class FieldExpressionNode extends ExpressionNode {

    IdentifierNode id;
    ExpressionNode expr;

    public FieldExpressionNode(IdentifierNode id, ExpressionNode expr) {
        super();
        this.id = id;
        this.id.setParent(this);
        this.expr = expr;
        this.expr.setParent(this);
    }

    @Override
    public String toString() {
        return "AssignField {field="+id+", value="+expr+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(id).add(expr).build();
    }
}
