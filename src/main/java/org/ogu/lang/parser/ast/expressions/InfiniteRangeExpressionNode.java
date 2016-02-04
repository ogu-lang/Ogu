package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

/**
 * expr...
 * Created by ediaz on 31-01-16.
 */
public class InfiniteRangeExpressionNode extends RangeExpressionNode {


    public InfiniteRangeExpressionNode(ExpressionNode begin) {
        super(begin);
    }

    @Override
    public String toString() {
        return "InfiniteRange{" +
                "begin=" + begin +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(begin);
    }
}
