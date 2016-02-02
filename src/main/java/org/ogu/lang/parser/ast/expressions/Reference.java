package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * A reference to a type, a val, a var o a function
 * Created by ediaz on 22-01-16.
 */
public class Reference extends Expression  {
    private OguIdentifier name;

    public Reference(OguIdentifier name) {
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
