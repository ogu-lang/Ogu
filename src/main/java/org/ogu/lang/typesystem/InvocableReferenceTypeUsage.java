package org.ogu.lang.typesystem;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalConstructorDefinition;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.symbols.FormalParameterSymbol;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 10/31/16.
 */
public class InvocableReferenceTypeUsage implements TypeUsage, Invocable {

    private InternalInvocableDefinition internalInvocableDefinition;

    public InvocableReferenceTypeUsage(InternalInvocableDefinition internalInvocableDefinition) {
        this.internalInvocableDefinition = internalInvocableDefinition;
    }

    @Override
    public Optional<? extends InternalInvocableDefinition> internalInvocableDefinitionFor(List<ActualParamNode> actualParamNodes) {
        return null;
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        if (typeParams.isEmpty()) {
            return this;
        }
        List<FormalParameterSymbol> replacedParams = internalInvocableDefinition
                .getFormalParameters()
                .stream()
                .map((fp) -> new FormalParameterSymbol(
                        fp.getType().replaceTypeVariables(typeParams),
                        fp.getName()))
                .collect(Collectors.toList());
        if (internalInvocableDefinition.isFunction()) {
            return new InvocableReferenceTypeUsage(new InternalFunctionDefinition(
                    internalInvocableDefinition.asFunction().getFunctionName(),
                    replacedParams,
                    internalInvocableDefinition.getReturnType().replaceTypeVariables(typeParams),
                    internalInvocableDefinition.asFunction().getJvmMethodDefinition()));
        } else if (internalInvocableDefinition.isConstructor()) {
            return new InvocableReferenceTypeUsage(new InternalConstructorDefinition(
                    internalInvocableDefinition.getReturnType().replaceTypeVariables(typeParams),
                    replacedParams,
                    internalInvocableDefinition.asConstructor().getJvmConstructorDefinition()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean sameType(TypeUsage other) {
        if (!other.isInvocable()) {
            return false;
        }
        if (!(other instanceof InvocableReferenceTypeUsage)) {
            return false;
        }
        InvocableReferenceTypeUsage otherInvokable = (InvocableReferenceTypeUsage)other;
        if (this.internalInvocableDefinition.getFormalParameters().size() !=
                otherInvokable.internalInvocableDefinition.getFormalParameters().size()) {
            return false;
        }
        for (int i=0; i<this.internalInvocableDefinition.getFormalParameters().size(); i++) {
            if (!this.internalInvocableDefinition.getFormalParameters().get(i).getType().sameType(
                    otherInvokable.internalInvocableDefinition.getFormalParameters().get(i).getType())) {
                return false;
            }
        }
        return this.internalInvocableDefinition.getReturnType().sameType(
                otherInvokable.internalInvocableDefinition.getReturnType());
    }

    @Override
    public boolean isInvocable() {
        return true;
    }

    @Override
    public Invocable asInvocable() {
        return this;
    }

    @Override
    public JvmType jvmType() {
        throw new UnsupportedOperationException("No tiene una correspondencia en la JVM");
    }

    @Override
    public boolean hasInstanceField(String fieldName, Symbol instance) {
        return false;
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        throw new IllegalArgumentException(describe() + " no tiene campo " + fieldName);
    }

    @Override
    public Optional<Invocable> getFunction(String function) {
        return Optional.empty();
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        return false;
    }


    @Override
    public String describe() {
        return  "(" +
                String.join(", " , internalInvocableDefinition.getFormalParameters().stream().map((fp)->fp.getType().describe()).collect(Collectors.toList())) + ") -> " +
                internalInvocableDefinition.getReturnType().describe();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvocableReferenceTypeUsage)) return false;

        InvocableReferenceTypeUsage that = (InvocableReferenceTypeUsage) o;

        if (!internalInvocableDefinition.equals(that.internalInvocableDefinition)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return internalInvocableDefinition.hashCode();
    }

    @Override
    public String toString() {
        return "InvocableReferenceTypeUsage{" +
                "internalInvocableDefinition=" + internalInvocableDefinition +
                '}';
    }
}
