package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists by extension
 * [a,b,c..d,e...]
 * Created by ediaz on 31-01-16.
 */
public class ListByExtensionExpression extends ListExpression {

    List<RangeExpression> ranges;

    public ListByExtensionExpression(List<RangeExpression> ranges) {
        super();
        this.ranges = new ArrayList<>();
        this.ranges.addAll(ranges);
        this.ranges.forEach((r) -> r.setParent(this));
    }

    @Override
    public String toString() {
        return "ListByExtension {" +
                "ranges = "+ranges +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ranges).build();
    }

}
