package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class TupleTypeArgNode extends TypeArgNode {

    private List<TypeArgNode> args;

    public TupleTypeArgNode(List<TypeArgNode> args) {
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
    public JvmType jvmType() {
        return null;
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        return false;
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return null;
    }
}
