package org.ogu.lang.typesystem;

import java.util.List;

/**
 * Created by ediaz on 11/6/16.
 */
public abstract class OverloadedFunctionReferenceTypeUsage implements TypeUsage, Invocable {

    protected List<InvocableReferenceTypeUsage> alternatives;

    OverloadedFunctionReferenceTypeUsage(List<InvocableReferenceTypeUsage> alternatives) {
        if (alternatives.size() < 2) {
            throw new IllegalArgumentException();
        }
        this.alternatives = alternatives;
    }

    @Override
    public Invocable asInvocable() {
        return this;
    }

    @Override
    public boolean isInvocable() {
        return true;
    }

}
