package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class VectorTypeArgUsageNode extends TypeArgUsageWrapperNode {

    private TypeArgUsageWrapperNode base;

    public VectorTypeArgUsageNode(TypeArgUsageWrapperNode base) {
        this.base = base;
        this.base.setParent(this);
    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public String toString() {
        return "["+base+']';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(base);
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }


}
