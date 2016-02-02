package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;

/**
 * record field
 * {name:String, age:Int}
 * Created by ediaz on 01-02-16.
 */
public class RecordField extends Node {

    private OguIdentifier name;
    private OguType type;

    public RecordField(OguIdentifier name, OguType type) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }

    public String toString() {
        return "Field{name="+name+", type="+type+'}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).add(type).build();
    }
}
