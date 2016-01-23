package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * A type reference appears in constructors
 * Created by ediaz on 23-01-16.
 */
public class TypeReference extends Expression {

    private OguTypeIdentifier type;

    public TypeReference(OguTypeIdentifier type) {
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {

        return "TypeReference{" +
                "type='" + type + '\'' +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(type);
    }
}
