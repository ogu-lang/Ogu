package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * $id <- value
 * Created by ediaz on 31-01-16.
 */
public class AssignSelfExpression extends AssignExpression {


    public AssignSelfExpression(OguIdentifier id, Expression value) {
        super(id, value);
    }

    public AssignSelfExpression(OguIdentifier id, Expression optArg, Expression value) {
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
