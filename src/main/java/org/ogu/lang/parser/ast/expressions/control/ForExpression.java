package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.expressions.SetConstraint;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Created by ediaz on 31-01-16.
 */
public class ForExpression extends Expression {
    private SetConstraint forCond;
    private DoExpression doBlock;

    public ForExpression(SetConstraint forCond, DoExpression doBlock) {
        super();
        this.forCond = forCond;
        this.doBlock = doBlock;
    }

    @Override
    public String toString() {
        return "For { cond=" + forCond + ", block=" + doBlock + '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(forCond).add(doBlock).build();
    }

}
