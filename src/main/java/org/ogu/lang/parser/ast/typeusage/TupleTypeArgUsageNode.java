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
public class TupleTypeArgUsageNode extends TypeArgUsageWrapperNode {

    private List<TypeArgUsageWrapperNode> args;

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    public TupleTypeArgUsageNode(List<TypeArgUsageWrapperNode> args) {
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
    }


    @Override
    public String toString() {
        return "("+args+')';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).build();
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
