package org.ogu.lang.parser.ast.expressions.literals;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * Created by ediaz on 27-01-16.
 */
public class DoubleLiteralNode extends ExpressionNode {

    double value;

    public DoubleLiteralNode(double value) {
        super();
        this.value = value;
    }

    public double getValue() { return value; }

    @Override
    public String toString() {
        return "DOUBLE("+value+")";
    }

    @Override
    public TypeUsage calcType() {
        return PrimitiveTypeUsage.DOUBLE;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
