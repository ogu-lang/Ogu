package org.ogu.lang.typesystem;

/**
 * Created by ediaz on 21-01-16.
 */
public interface TypeUsage {

    default boolean isInvocable() {
        return false;
    }

    default Invocable asInvocable() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + ": " + this);
    }
}
