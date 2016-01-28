package org.ogu.lang.parser.ast.expressions.literals;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.typesystem.TypeUsage;

import java.math.BigInteger;
import java.util.Collections;

/**
 * Created by ediaz on 27-01-16.
 */
public class IntLiteral extends Expression {

    BigInteger value;

    public IntLiteral(BigInteger value) {
        super();
        this.value = value;
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
