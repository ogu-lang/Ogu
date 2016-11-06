package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * An id used as parameter for a type
 * Created by ediaz on 23-01-16.
 */
public  class IdTypeArgUsageNode extends TypeUsageWrapperNode {

    private IdentifierNode id;


    public IdTypeArgUsageNode(IdentifierNode id) {
        super();
        this.id = id;
        this.id.setParent(this);
    }


    @Override
    public TypeUsageNode copy() {
        return null;
    }

    public String getName() {
        return id.getName();
    }

    @Override
    public String toString() {
        return "TypeArg{"+id+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(id);
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
