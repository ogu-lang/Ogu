package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * Created by ediaz on 30-01-16.
 */
public class VectorExpression extends Expression {

    private List<Expression> elements;
    private Expression result;
    private List<Expression> guards;

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return null;
    }
}
