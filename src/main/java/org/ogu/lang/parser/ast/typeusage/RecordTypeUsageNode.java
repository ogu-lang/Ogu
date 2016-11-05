package org.ogu.lang.parser.ast.typeusage;


import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Anonymous record type
 * Ej: {name:String, age:Int}
 * Created by ediaz on 01-02-16.
 */
public class RecordTypeUsageNode extends TypeUsageWrapperNode {

    private TypeIdentifierNode name;
    private List<RecordFieldNode> fields;

    public RecordTypeUsageNode(TypeIdentifierNode name, List<RecordFieldNode> fields) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.fields = new ArrayList<>();
        this.fields.addAll(fields);
        this.fields.forEach((f) -> f.setParent(this));
    }


    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public String toString() {
        return "Record{name="+name+", fields" + fields + '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).addAll(fields).build();
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
