package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.Collections;

/**
 * Native types: i8, u8, i16, u16, i32, u32, i64, u64, f32, f64
 * Created by ediaz on 24-01-16.
 */
public class NativeTypeUsageNode extends TypeUsageWrapperNode {

    private String nativeType;

    public NativeTypeUsageNode(String nativeType) {
        super(PrimitiveTypeUsage.getByName(nativeType));
        this.nativeType =  nativeType;
    }


    @Override
    public TypeUsage calcType() {
        return this;
    }

    @Override
    public int hashCode() {
        int result = nativeType.hashCode();
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NativeTypeUsageNode that = (NativeTypeUsageNode) o;

        if (!nativeType.equals(that.nativeType)) return false;

        return true;
    }

    @Override
    public TypeUsageNode copy() {
        NativeTypeUsageNode copy = new NativeTypeUsageNode(this.nativeType);
        copy.parent = this.parent;
        return copy;

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
