package org.ogu.lang.parser.ast.decls.typedef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.typeusage.TypeUsageWrapperNode;

import java.util.Collections;

/**
 * Used in generic types
 * Created by ediaz on 24-01-16.
 */
public class TypeParamNode extends Node {

    protected String id;
    protected TypeUsageWrapperNode type;

    public TypeParamNode(String id) {
        this.id = id;
    }

    public TypeParamNode(TypeUsageWrapperNode type) {
        this.type = type;
        this.type.setParent(this);
    }


    @Override
    public String toString() {
        return "TypeParam {" +
                "id=" + id +
                ", type=" + type +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return Collections.emptyList();
        else
            return ImmutableList.of(type);
    }
}
