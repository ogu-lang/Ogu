package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.*;
import org.ogu.lang.util.Logger;

import java.util.Map;
import java.util.Optional;

/**
 * OguType is the base of all type declarations
 * Created by ediaz on 24-01-16.
 */
public abstract class TypeUsageWrapperNode extends TypeUsageNode  {

    protected TypeUsage typeUsage;

    @Override
    public Optional<Invocable> getFunction(String function) {
        return typeUsage().getFunction(function);
    }


    @Override
    public String toString() {
        return "TypeNode{" +
                "typeUsage=" + typeUsage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeUsageWrapperNode)) return false;

        TypeUsageWrapperNode that = (TypeUsageWrapperNode) o;

        if (typeUsage != null ? !typeUsage.equals(that.typeUsage) : that.typeUsage != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return typeUsage != null ? typeUsage.hashCode() : 0;
    }


    public TypeUsage typeUsage() {
        if (typeUsage == null) {
            Logger.debug("type usage null en "+this.getClass());
            throw new IllegalStateException();
        }
        return typeUsage;
    }

    public TypeUsageWrapperNode(TypeUsage typeUsage) {
        this.typeUsage = typeUsage;
    }

    public TypeUsageWrapperNode() {
        this.typeUsage = null;
    }

    @Override
    public final boolean isReferenceTypeUsage() {
        return typeUsage().isReferenceTypeUsage();
    }

    @Override
    public final ReferenceTypeUsage asReferenceTypeUsage() {
        return typeUsage().asReferenceTypeUsage();
    }

    @Override
    public final boolean isArray() {
        return typeUsage().isArray();
    }

    @Override
    public final boolean isPrimitive() {
        return typeUsage().isPrimitive();
    }

    @Override
    public final boolean isReference() {
        return typeUsage().isReference();
    }

    @Override
    public final boolean isVoid() {
        return typeUsage().isVoid();
    }

    @Override
    public final JvmType jvmType() {
        return typeUsage().jvmType();
    }

    @Override
    public final ArrayTypeUsage asArrayTypeUsage() {
        return this.typeUsage().asArrayTypeUsage();
    }

    @Override
    public final boolean canBeAssignedTo(TypeUsage type) {
        Logger.debug("CAN BE? "+type+" this = "+this);
        return typeUsage().canBeAssignedTo(type);
    }

    @Override
    public final Symbol getInstanceField(String fieldName, Symbol instance) {
        return typeUsage().getInstanceField(fieldName, instance);
    }

    @Override
    public final PrimitiveTypeUsage asPrimitiveTypeUsage() {
        return typeUsage().asPrimitiveTypeUsage();
    }

    @Override
    public final <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return typeUsage().replaceTypeVariables(typeParams);
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return typeUsage().sameType(other);
    }

    public String getName()  {
        return null;
    }

}
