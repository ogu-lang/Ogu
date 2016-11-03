package org.ogu.lang.definitions;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ediaz on 21-01-16.
 */
public class InternalConstructorDefinition extends InternalInvocableDefinition {

    private JvmConstructorDefinition jvmConstructorDefinition;
    private TypeUsage returnType;

    public JvmConstructorDefinition getJvmConstructorDefinition() {
        return jvmConstructorDefinition;
    }

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

    @Override
    public InternalConstructorDefinition apply(Map<String, TypeUsage> typeParams) {
        List<FormalParameter> formalParametersReplaced = new ArrayList<>();
        for (FormalParameter fp : getFormalParameters()) {
            formalParametersReplaced.add(fp.apply(typeParams));
        }
        return new InternalConstructorDefinition(returnType.replaceTypeVariables(typeParams),
                formalParametersReplaced, jvmConstructorDefinition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InternalConstructorDefinition)) return false;

        InternalConstructorDefinition that = (InternalConstructorDefinition) o;

        if (!jvmConstructorDefinition.equals(that.jvmConstructorDefinition)) return false;
        if (!returnType.equals(that.returnType)) return false;
        if (!getFormalParameters().equals(that.getFormalParameters())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jvmConstructorDefinition.hashCode();
        result = 31 * result + returnType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InternalConstructorDefinition{" +
                "jvmConstructorDefinition=" + jvmConstructorDefinition +
                ", returnType=" + returnType +
                '}';
    }

}
