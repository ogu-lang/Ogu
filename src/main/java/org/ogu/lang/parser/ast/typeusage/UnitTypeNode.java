package org.ogu.lang.parser.ast.typeusage;

import java.util.Collections;
import java.util.Map;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * ()
 * Created by ediaz on 30-01-16.
 */
public class UnitTypeNode extends TypeNode {

    public UnitTypeNode() {
        super();
    }

    @Override
    public boolean isVoid() { return true; }


    @Override
    public String toString() {
        return "Unit!";
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public JvmType jvmType() {
        return JvmType.VOID;
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return typeUsage().sameType(other);
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        return typeUsage().canBeAssignedTo(type);
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return null;
    }
}
