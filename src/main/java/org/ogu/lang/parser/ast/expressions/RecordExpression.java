package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Map : {a->b, c->d}
 * Created by ediaz on 01-02-16.
 */
public class RecordExpression extends DictExpression {

    private List<FieldExpression> fields;

    public RecordExpression(List<FieldExpression> fields) {
        super();
        this.fields = new ArrayList<>();
        this.fields.addAll(fields);
        this.fields.forEach((f) -> f.setParent(this));
    }

    @Override
    public String toString() {
        return "Record {fields="+fields+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(fields).build();
    }
}
