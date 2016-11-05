package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class TupleTypeUsageNode extends TypeUsageWrapperNode {

    private List<TypeUsageWrapperNode> bases;

    public TupleTypeUsageNode(List<TypeUsageWrapperNode> bases) {
        this.bases = new ArrayList<>();
        this.bases.addAll(bases);
        this.bases.forEach((b) -> b.setParent(this));
    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    public List<TypeUsageWrapperNode> getBases() {
        return bases;
    }

    public TypeUsageWrapperNode getBase(int i) {
        return bases.get(i);
    }

    @Override
    public String toString() {
        return "("+bases+')';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(bases).build();
    }



    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
