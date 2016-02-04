package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * expr..expr
 * Created by ediaz on 31-01-16.
 */
public class RangeExpressionNode extends ExpressionNode {

    ExpressionNode begin;
    ExpressionNode end;

    protected RangeExpressionNode(ExpressionNode begin) {
        super();
        this.begin = begin;
        this.begin.setParent(this);
    }

    public RangeExpressionNode(ExpressionNode begin, ExpressionNode end) {
        this.begin = begin;
        this.begin.setParent(this);
        this.end = end;
        this.end.setParent(this);
    }

    @Override
    public String toString() {
        return "Range{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(begin).add(end).build();
    }

}
