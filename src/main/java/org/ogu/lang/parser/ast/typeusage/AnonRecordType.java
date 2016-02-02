package org.ogu.lang.parser.ast.typeusage;


import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Anonymous record type
 * Ej: {name:String, age:Int}
 * Created by ediaz on 01-02-16.
 */
public class AnonRecordType extends OguType {
  private List<RecordField> fields;

    public AnonRecordType(List<RecordField> fields) {
        super();
        this.fields = new ArrayList<>();
        this.fields.addAll(fields);
        this.fields.forEach((f) -> f.setParent(this));
    }


    @Override
    public String toString() {
        return "AnonRecord{fields:"+fields+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(fields).build();
    }
}
