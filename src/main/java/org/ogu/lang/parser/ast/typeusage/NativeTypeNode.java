package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;
import java.util.Map;

/**
 * Native types: i8, u8, i16, u16, i32, u32, i64, u64, f32, f64
 * Created by ediaz on 24-01-16.
 */
public class NativeTypeNode extends TypeNode {

    private String nativeType;

    public NativeTypeNode(String nativeType) {
        this.nativeType =  nativeType;

    }

    @Override
    public String toString() {
        return nativeType;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
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
