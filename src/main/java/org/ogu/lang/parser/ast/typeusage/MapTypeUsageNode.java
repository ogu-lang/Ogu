package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class MapTypeUsageNode extends TypeUsageWrapperNode {

    private TypeUsageWrapperNode key;
    private TypeUsageWrapperNode val;

    public MapTypeUsageNode(TypeUsageWrapperNode key, TypeUsageWrapperNode val) {
        super();
        this.key = key;
        this.key.setParent(this);
        this.val = val;
        this.val.setParent(this);
    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public String toString() {
        return "{"+key+"->"+val+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(key).add(val).build();
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
