package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;
import java.util.Map;

/**
 * Unit type (def foo : -> ())
 * Created by ediaz on 23-01-16.
 */
public class UnitTypeArgNode extends TypeArgNode {

    public UnitTypeArgNode() {

    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Unit!";
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
    public final <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return typeUsage().replaceTypeVariables(typeParams);
    }

}
