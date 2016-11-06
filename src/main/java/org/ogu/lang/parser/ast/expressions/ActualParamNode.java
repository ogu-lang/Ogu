package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

/**
 * Parameters passed to a function call
 * Created by ediaz on 21-01-16.
 */
public class ActualParamNode extends Node {

    private ExpressionNode value;

    public ExpressionNode getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActualParamNode)) return false;

        ActualParamNode that = (ActualParamNode) o;
        return value.equals(that.value);

    }



    @Override
    public String toString() {
        return "ActualParam{" +
                "value=" + value +
                '}';
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public ActualParamNode(ExpressionNode value) {
        this.value = value;
        this.value.setParent(this);
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(value);
    }



}
