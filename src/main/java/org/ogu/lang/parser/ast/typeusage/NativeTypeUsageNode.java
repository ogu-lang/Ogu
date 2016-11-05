package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;

/**
 * Native types: i8, u8, i16, u16, i32, u32, i64, u64, f32, f64
 * Created by ediaz on 24-01-16.
 */
public class NativeTypeUsageNode extends TypeUsageWrapperNode {

    private String nativeType;

    public NativeTypeUsageNode(String nativeType) {
        this.nativeType =  nativeType;

    }
    @Override
    public TypeUsageNode copy() {
        return null;
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
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
