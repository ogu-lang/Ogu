package org.ogu.lang.typesystem;

import javassist.CtMethod;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.compiled.JavassistBasedMethodResolution;
import org.ogu.lang.symbols.Symbol;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 11/6/16.
 */
public class OverloadedInvocableReferenceTypeUsage  extends OverloadedFunctionReferenceTypeUsage  {


    private List<CtMethod> ctMethods;
    private String methodName;
    private boolean staticContext;
    private SymbolResolver resolver;

    public OverloadedInvocableReferenceTypeUsage(List<InvocableReferenceTypeUsage> alternatives,
                                                 List<CtMethod> ctMethods,
                                                 SymbolResolver resolver) {
        super(alternatives);
        if (alternatives.size() != ctMethods.size()) {
            throw new IllegalArgumentException();
        }
        this.resolver = resolver;
        methodName = ctMethods.get(0).getName();
        staticContext = Modifier.isStatic(ctMethods.get(0).getModifiers());
        for (CtMethod method : ctMethods) {
            if (!method.getName().equals(methodName)) {
                throw new IllegalArgumentException();
            }
            if (Modifier.isStatic(method.getModifiers()) != staticContext) {
                throw new IllegalArgumentException();
            }
        }
        this.ctMethods = ctMethods;
    }

    @Override
    public boolean sameType(TypeUsage other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOverloaded() {
        return true;
    }


    @Override
    public Optional<? extends InternalInvocableDefinition> internalInvocableDefinitionFor(List<ActualParamNode> actualParams) {
        List<JvmType> argsTypes = new ArrayList<>();
        for (ActualParamNode actualParam : actualParams) {
                argsTypes.add(actualParam.getValue().calcType().jvmType());
        }
        CtMethod method = JavassistBasedMethodResolution.findMethodAmong(methodName, argsTypes, resolver, ctMethods);
        int index = ctMethods.indexOf(method);
        if (index == -1) {
            throw new RuntimeException();
        }
        return alternatives.get(index).internalInvocableDefinitionFor(actualParams);
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JvmType jvmType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        throw new UnsupportedOperationException();
    }
}
