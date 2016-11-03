package org.ogu.lang.definitions;

import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;
import java.util.Map;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class InternalInvocableDefinition {

    private List<? extends FormalParameter> formalParameters;

    public InternalInvocableDefinition(List<? extends FormalParameter> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public List<? extends FormalParameter> getFormalParameters() {
        return formalParameters;
    }

    public abstract InternalConstructorDefinition asConstructor();

    public abstract InternalFunctionDefinition asFunction();

    public abstract boolean isConstructor();

    public abstract boolean isFunction();

    public abstract TypeUsage getReturnType();

    public abstract InternalInvocableDefinition apply(Map<String, TypeUsage> typeParams);
}
