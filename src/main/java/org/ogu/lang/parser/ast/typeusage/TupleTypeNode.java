package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class TupleTypeNode extends TypeNode {

    private List<TypeNode> bases;

    public TupleTypeNode(List<TypeNode> bases) {
        this.bases = new ArrayList<>();
        this.bases.addAll(bases);
        this.bases.forEach((b) -> b.setParent(this));
    }


    @Override
    public String toString() {
        return "("+bases+')';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(bases).build();
    }


}
