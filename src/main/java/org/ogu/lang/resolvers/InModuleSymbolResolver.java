package org.ogu.lang.resolvers;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasJvmInteropDeclarationNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: Complete
 * Created by ediaz on 20-01-16.
 */
public class InModuleSymbolResolver implements SymbolResolver {


    private TypeResolver typeResolver;

    @Override
    public String toString() {
        return "InModuleSymbolResolver{" +
                "typeResolver=" + typeResolver +
                ", parent=" + parent +
                '}';
    }

    private SymbolResolver parent = null;

    @Override
    public SymbolResolver getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolResolver parent) {
        this.parent = parent;
    }

    public InModuleSymbolResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.typeResolver.setSymbolResolver(this);
    }



    @Override
    public Optional<Symbol> findSymbol(String name, Node context) {
        if (context == null) {
            Optional<TypeDefinition> typeDefinition = typeResolver.resolveAbsoluteTypeName(name);
            if (typeDefinition.isPresent()) {
                return Optional.of(typeDefinition.get());
            }
            return Optional.empty();
        } else {
            return context.findSymbol(name, this);
        }
    }

    @Override
    public Optional<TypeDefinition> findTypeDefinitionIn(String typeName, Node context, SymbolResolver resolver) {
        // primitive names are not valid here
        if (!JvmNameUtils.isValidQualifiedName(typeName)) {
            throw new IllegalArgumentException(typeName);
        }
        return findTypeDefinitionInHelper(typeName, context, null, resolver);
    }

    @Override
    public Optional<TypeDefinition> findTypeDefinitionFromJvmSignature(String jvmSignature, Node context, SymbolResolver resolver) {
        if (!jvmSignature.contains(":")) {
            return findTypeDefinitionIn(jvmSignature, context, resolver);
        }

        String[] parts = jvmSignature.split(":");
        if (parts.length != 2) {
            throw new IllegalStateException("invalid jvmSignature "+jvmSignature);
        }
        String[] parts2 = parts[0].split("/");
        if (parts2.length != 2) {
            throw new IllegalStateException("invalid jvmSignature "+jvmSignature);
        }
        return findTypeDefinitionIn(parts2[0], context, resolver);
    }

    @Override
    public Optional<JvmMethodDefinition> findJvmDefinition(FunctionCallNode functionCall) {
        List<ActualParamNode> argsTypes = functionCall.getActualParamValuesInOrder().stream()
                .map((e) -> {
                    ActualParamNode ap = new ActualParamNode(e);
                    ap.setParent(functionCall);
                    return ap;
                })
                .collect(Collectors.toList());
        ExpressionNode function = functionCall.getFunction();
        return Optional.of(function.findFunctionFor(argsTypes, this));
    }

    @Override
    public boolean existPackage(String packageName) {
        return typeResolver.existPackage(packageName);
    }


    private Optional<TypeDefinition> findTypeDefinitionInHelper(String typeName, Node context,
                                                                Node previousContext, SymbolResolver resolver) {

        if (!JvmNameUtils.isValidQualifiedName(typeName)) {
            throw new IllegalArgumentException(typeName);
        }
        if (context == null) {
            // implicitly look into java.lang package
            Optional<TypeDefinition> result = typeResolver.resolveAbsoluteTypeName("java.lang." + typeName);
            if (result.isPresent()) {
                return result;
            }

            return typeResolver.resolveAbsoluteTypeName(typeName);
        }
        if (context instanceof AliasJvmInteropDeclarationNode) {
            Optional<TypeDefinition> result = typeResolver.resolveAbsoluteTypeName(typeName);
            if (result.isPresent()) {
                return result;
            }
            return typeResolver.resolveAbsoluteTypeName(typeName);
        }
        for (Node child : context.getChildren()) {
            if (child instanceof TypeDefinition) {
                TypeDefinition typeDefinition = (TypeDefinition)child;
                if (typeDefinition.getName().equals(typeName)
                        || typeDefinition.getQualifiedName().equals(typeName)) {
                    return Optional.of(typeDefinition);
                }
            }
        }
        if (!context.contextName().isEmpty()) {
            String qName = context.contextName() + "." + typeName;
            Optional<TypeDefinition>  partial = getRoot().findTypeDefinitionIn(qName, null, getRoot());
            if (partial.isPresent()) {
                return partial;
            }
        }
        return findTypeDefinitionInHelper(typeName, context.getParent(), context, resolver);
    }
}
