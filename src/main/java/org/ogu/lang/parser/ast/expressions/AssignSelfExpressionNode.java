package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.IdentifierNode;

/**
 * $id <- value
 * Created by ediaz on 31-01-16.
 */
public class AssignSelfExpressionNode extends AssignExpressionNode {


    public AssignSelfExpressionNode(IdentifierNode id, ExpressionNode value) {
        super(id, value);
    }

    public AssignSelfExpressionNode(IdentifierNode id, ExpressionNode optArg, ExpressionNode value) {
        super(id, optArg, value);
    }

    @Override
    public String toString() {
        return "AssignSelf {" +
                "id = "+id +
                "opt_arg = "+optArg +
                "value = "+value +
                '}';
    }


}
