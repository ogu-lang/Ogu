package org.ogu.lang.parser.ast.expressions.literals;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.math.BigInteger;
import java.util.Collections;

/**
 * Created by ediaz on 27-01-16.
 */
public class IntLiteralNode extends ExpressionNode {

    BigInteger value;

    public IntLiteralNode(BigInteger value) {
        super();
        this.value = value;
    }

    @Override
    public String toString() {
        return "INT("+value+")";
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
