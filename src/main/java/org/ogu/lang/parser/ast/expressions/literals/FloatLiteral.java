package org.ogu.lang.parser.ast.expressions.literals;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.typesystem.TypeUsage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

/**
 * Created by ediaz on 27-01-16.
 */
public class FloatLiteral extends Expression {

    BigDecimal value;

    public FloatLiteral(BigDecimal value) {
        super();
        this.value = value;
    }

    @Override
    public String toString() {
        return "FLOAT("+value+")";
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
