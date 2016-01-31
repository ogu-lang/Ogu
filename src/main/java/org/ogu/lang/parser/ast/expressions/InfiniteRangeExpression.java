package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

/**
 * expr...
 * Created by ediaz on 31-01-16.
 */
public class InfiniteRangeExpression extends RangeExpression {


    public InfiniteRangeExpression(Expression begin) {
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
        return ImmutableList.of(end);
    }
}
