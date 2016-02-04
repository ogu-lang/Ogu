package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;

/**
 * case s of
 *    cond => result
 *    ....
 * Created by ediaz on 30-01-16.
 */
public class CaseGuardNode extends Node {

    private ExpressionNode cond;
    private ExpressionNode result;

    public CaseGuardNode(ExpressionNode cond, ExpressionNode result) {
        this.cond = cond;
        this.cond.setParent(this);
        this.result = result;
        this.result.setParent(this);
    }

    @Override
    public String toString() {
        return "CaseGuard {"+
                "cond="+cond+
                ", result="+result+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(cond).add(result).build();

    }
}
