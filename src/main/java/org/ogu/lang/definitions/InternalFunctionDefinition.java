package org.ogu.lang.definitions;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ediaz on 21-01-16.
 */
public class InternalFunctionDefinition extends InternalInvocableDefinition {

    private String functionName;
    private JvmMethodDefinition jvmMethodDefinition;
    private TypeUsage returnType;


    public InternalFunctionDefinition(String functionName, List<? extends FormalParameter> formalParameters, TypeUsage returnType, JvmMethodDefinition jvmMethodDefinition) {
        super(formalParameters);
        this.functionName = functionName;
        this.returnType = returnType;
        this.jvmMethodDefinition = jvmMethodDefinition;
    }


    public String getFunctionName() { return functionName; }

    public JvmMethodDefinition getJvmMethodDefinition() {
        return jvmMethodDefinition;
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

    @Override
    public InternalInvocableDefinition apply(Map<String, TypeUsage> typeParams) {
        List<FormalParameter> formalParametersReplaced = new ArrayList<>();
        for (FormalParameter fp : getFormalParameters()) {
            formalParametersReplaced.add(fp.apply(typeParams));
        }
        return new InternalFunctionDefinition(functionName,
                formalParametersReplaced, returnType.replaceTypeVariables(typeParams), jvmMethodDefinition);
    }
}
