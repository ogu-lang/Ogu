package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;

/**
 * {}
 * Created by ediaz on 01-02-16.
 */
public class EmptyMapExpressionNode extends DictExpressionNode {

    public EmptyMapExpressionNode() {
        super();
    }

    @Override
    public String toString() {
        return "EmptyMap {}";
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
