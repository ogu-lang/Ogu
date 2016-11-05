package org.ogu.lang.parser.ast.typeusage;

import java.util.Collections;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.typesystem.VoidTypeUsage;

/**
 * ()
 * Created by ediaz on 30-01-16.
 */
public class UnitTypeUsageNode extends TypeUsageWrapperNode {

    public UnitTypeUsageNode() {
        super(new VoidTypeUsage());
    }

    @Override
    public TypeUsageNode copy() {
        return this;
    }

    @Override
    public String toString() {
        return "UnitTypeUsage!";
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return null;
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return typeUsage().sameType(other);
    }

}
