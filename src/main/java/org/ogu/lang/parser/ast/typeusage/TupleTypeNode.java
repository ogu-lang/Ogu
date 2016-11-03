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
public class TupleTypeNode extends TypeNode {

    private List<TypeNode> bases;

    public TupleTypeNode(List<TypeNode> bases) {
        this.bases = new ArrayList<>();
        this.bases.addAll(bases);
        this.bases.forEach((b) -> b.setParent(this));
    }


    public List<TypeNode> getBases() {
        return bases;
    }

    public TypeNode getBase(int i) {
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
