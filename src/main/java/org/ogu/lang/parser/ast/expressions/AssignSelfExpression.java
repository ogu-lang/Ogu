package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.IdentifierNode;

/**
 * $id <- value
 * Created by ediaz on 31-01-16.
 */
public class AssignSelfExpression extends AssignExpression {


    public AssignSelfExpression(IdentifierNode id, Expression value) {
        super(id, value);
    }

    public AssignSelfExpression(IdentifierNode id, Expression optArg, Expression value) {
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
