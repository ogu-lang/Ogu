package org.ogu.lang.parser.ast.expressions.control;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * case expr of g => e ...
 * Created by ediaz on 30-01-16.
 */
public class CaseExpressionNode extends ExpressionNode {

    private ExpressionNode selector;
    private List<CaseGuard> guards;

    public CaseExpressionNode(ExpressionNode selector, List<CaseGuard> guards) {
        super();
        this.selector = selector;
        this.selector.setParent(this);
        this.guards = new ArrayList<>();
        this.guards.addAll(guards);
        this.guards.forEach((g) -> g.setParent(this));
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(selector).addAll(guards).build();

    }



    @Override
    public String toString() {
        return "Case{" +
                "selector='" + selector + '\'' +
                ", guards=" + guards +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }
}
