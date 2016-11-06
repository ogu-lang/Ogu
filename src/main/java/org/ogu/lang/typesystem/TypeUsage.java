package org.ogu.lang.typesystem;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 21-01-16.
 */
public interface TypeUsage {


    default boolean isArray() { return false; }

    default boolean isPrimitive() { return false;   }

    default boolean isReferenceTypeUsage() { return false; }

    default boolean isVoid() { return false; }

    default boolean isTypeVariable() { return false; }

    default ReferenceTypeUsage asReferenceTypeUsage() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    default ArrayTypeUsage asArrayTypeUsage() {
        throw new UnsupportedOperationException();
    }

    default PrimitiveTypeUsage asPrimitiveTypeUsage() {
        throw new UnsupportedOperationException();
    }

    default TypeVariableUsage asTypeVariableUsage() {
        throw new UnsupportedOperationException();
    }

    default boolean isReference() { return false; }

    default boolean isInvocable() {
        return false;
    }

    default Invocable asInvocable() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + ": " + this);
    }

    JvmType jvmType();

    default boolean hasInstanceField(String fieldName, Symbol instance) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    default Symbol getInstanceField(String fieldName, Symbol instance) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }


    default Optional<Invocable> getFunction(String function) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    boolean sameType(TypeUsage other);

    boolean canBeAssignedTo(TypeUsage type);

    <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams);

    default String describe() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }


}
