package org.ogu.lang.parser.ast.typeusage;


import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Anonymous record type
 * Ej: {name:String, age:Int}
 * Created by ediaz on 01-02-16.
 */
public class RecordType extends OguType {

    private OguTypeIdentifier name;
    private List<RecordField> fields;

    public RecordType(OguTypeIdentifier name, List<RecordField> fields) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.fields = new ArrayList<>();
        this.fields.addAll(fields);
        this.fields.forEach((f) -> f.setParent(this));
    }


    @Override
    public String toString() {
        return "Record{name="+name+", fields" + fields + '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).addAll(fields).build();
    }
}
