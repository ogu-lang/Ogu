package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Map;


/**
 * A type like M1.M2.T
 * Created by ediaz on 23-01-16.
 */
public class QualifiedTypeNode extends TypeNode {

    private TypeIdentifierNode typeId;

    public QualifiedTypeNode(TypeIdentifierNode typeId) {
        this.typeId = typeId;
        this.typeId.setParent(this);
    }

    @Override
    public String toString() {
        return typeId.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(typeId);
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
