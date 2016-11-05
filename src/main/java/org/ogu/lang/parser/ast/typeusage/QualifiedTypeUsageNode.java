package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;


/**
 * A type like M1.M2.T
 * Created by ediaz on 23-01-16.
 */
public class QualifiedTypeUsageNode extends TypeUsageWrapperNode {

    private TypeIdentifierNode typeId;

    public QualifiedTypeUsageNode(TypeIdentifierNode typeId) {
        this.typeId = typeId;
        this.typeId.setParent(this);
    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public String toString() {
        return typeId.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(typeId);
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
