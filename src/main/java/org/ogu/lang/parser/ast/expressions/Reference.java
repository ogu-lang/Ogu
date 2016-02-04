package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * A reference to a type, a val, a var o a function
 * Created by ediaz on 22-01-16.
 */
public class Reference extends ExpressionNode {
    private IdentifierNode name;

    public Reference(IdentifierNode name) {
        this.name = name;
        this.name.setParent(this);
    }

    @Override
    public String toString() {

        return "Reference{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(name);
    }
}
