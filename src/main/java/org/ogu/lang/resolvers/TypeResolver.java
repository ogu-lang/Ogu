package org.ogu.lang.resolvers;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.invocables.FunctionDefinitionNode;

import java.util.Optional;

/**
 * Type Resolver
 * Created by ediaz on 20-01-16.
 */
public interface TypeResolver {

    public TypeResolver root();
    public void setRoot(TypeResolver root);

    public Optional<TypeDefinition> resolveAbsoluteTypeName(String typeName);
    public Optional<FunctionDefinitionNode> resolveAbsoluteFunctionName(String typeName);

    boolean existPackage(String packageName);

    SymbolResolver symbolResolver();
    void setSymbolResolver(SymbolResolver symbolResolver);
}