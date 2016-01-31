package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * do expr | do expr_block
 * Created by ediaz on 31-01-16.
 */
public class DoExpression extends Expression {

    List<Expression> exprs;

    public DoExpression(List<Expression> exprs) {
        super();
        this.exprs = new ArrayList<>();
        this.exprs.addAll(exprs);
        this.exprs.forEach((e) -> e.setParent(this));
    }


    @Override
    public String toString() {
        return "Do { exprs=" + exprs + '}';
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
