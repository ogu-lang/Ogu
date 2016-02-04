package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class MapTypeNode extends TypeNode {

    private TypeNode key;
    private TypeNode val;

    public MapTypeNode(TypeNode key, TypeNode val) {
        super();
        this.key = key;
        this.key.setParent(this);
        this.val = val;
        this.val.setParent(this);
    }


    @Override
    public String toString() {
        return "{"+key+"->"+val+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(key).add(val).build();
    }
}
