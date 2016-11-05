package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;

/**
 * Unit type (def foo : -> ())
 * Created by ediaz on 23-01-16.
 */
public class UnitTypeArgUsageNode extends TypeArgUsageWrapperNode {

    public UnitTypeArgUsageNode() {

    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Unit!";
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return typeUsage().sameType(other);
    }


}
