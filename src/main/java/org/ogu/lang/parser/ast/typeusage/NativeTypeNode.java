package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

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
}
