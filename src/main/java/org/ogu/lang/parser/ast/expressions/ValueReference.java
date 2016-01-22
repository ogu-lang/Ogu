package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Created by ediaz on 22-01-16.
 */
public class ValueReference extends Expression  {
    private String name;

    public ValueReference(String name) {
        this.name = name;
    }

    @Override
    public String toString() {

        return "ValueReference{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of();
    }
}
