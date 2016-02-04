package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * set id = value
 * id <- value
 * id v <- value
 * set id v <- value
 * Created by ediaz on 31-01-16.
 */
public class AssignExpressionNode extends ExpressionNode {

    protected IdentifierNode id;
    protected ExpressionNode optArg;
    protected ExpressionNode value;


    public AssignExpressionNode(IdentifierNode id, ExpressionNode value) {
        super();
        this.id = id;
        this.id.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    public AssignExpressionNode(IdentifierNode id, ExpressionNode optArg, ExpressionNode value) {
        super();
        this.id = id;
        this.id.setParent(this);
        this.optArg = optArg;
        this.optArg.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public String toString() {
        return "Assign {" +
                "id = "+id +
                "opt_arg = "+optArg +
                "value = "+value +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        if (optArg != null)
            return ImmutableList.<Node>builder().add(id).add(optArg).add(value).build();
        else
            return ImmutableList.<Node>builder().add(id).add(value).build();
    }

}
