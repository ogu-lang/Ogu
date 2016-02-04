package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.SetConstraintNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Created by ediaz on 31-01-16.
 */
public class ForExpressionNode extends ExpressionNode {
    private SetConstraintNode forCond;
    private DoExpressionNode doBlock;

    public ForExpressionNode(SetConstraintNode forCond, DoExpressionNode doBlock) {
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
