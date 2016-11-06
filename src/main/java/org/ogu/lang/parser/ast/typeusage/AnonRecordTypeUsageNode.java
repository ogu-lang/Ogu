package org.ogu.lang.parser.ast.typeusage;


import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Anonymous record type
 * Ej: {name:String, age:Int}
 * Created by ediaz on 01-02-16.
 */
public class AnonRecordTypeUsageNode extends TypeUsageWrapperNode {
  private List<RecordFieldNode> fields;

    public AnonRecordTypeUsageNode(List<RecordFieldNode> fields) {
        super();
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
        return "AnonRecord{fields="+fields+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(fields).build();
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }


}
