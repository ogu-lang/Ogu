package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;

/**
 * Created by ediaz on 31-01-16.
 */
public class EmptyListExpression extends ListExpression {

    public EmptyListExpression() {
        super();
    }

    @Override
    public String toString() {
        return "EmptyList []";
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
