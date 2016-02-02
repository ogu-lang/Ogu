package org.ogu.lang.definitions;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public class InternalConstructorDefinition extends InternalInvocableDefinition {

    private JvmConstructorDefinition jvmConstructorDefinition;
    private TypeUsage returnType;

    public InternalConstructorDefinition(TypeUsage returnType, List<? extends FormalParameter> formalParameters, JvmConstructorDefinition jvmConstructorDefinition) {
        super(formalParameters);
        this.jvmConstructorDefinition = jvmConstructorDefinition;
        this.returnType = returnType;
    }


    @Override
    public InternalConstructorDefinition asConstructor() {
        return this;
    }

    @Override
    public InternalFunctionDefinition asFunction() {
        return null;
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public TypeUsage getReturnType() {
        return returnType;
    }
}
