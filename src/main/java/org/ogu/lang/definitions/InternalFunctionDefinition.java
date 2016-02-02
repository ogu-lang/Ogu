package org.ogu.lang.definitions;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public class InternalFunctionDefinition extends InternalInvocableDefinition {

    private String methodName;
    private JvmMethodDefinition jvmMethodDefinition;
    private TypeUsage returnType;


    public InternalFunctionDefinition(String methodName, List<? extends FormalParameter> formalParameters, TypeUsage returnType, JvmMethodDefinition jvmMethodDefinition) {
        super(formalParameters);
        this.methodName = methodName;
        this.returnType = returnType;
        this.jvmMethodDefinition = jvmMethodDefinition;
    }

    @Override
    public InternalConstructorDefinition asConstructor() {
        return null;
    }

    @Override
    public InternalFunctionDefinition asFunction() {
        return this;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public TypeUsage getReturnType() {
        return returnType;
    }
}
