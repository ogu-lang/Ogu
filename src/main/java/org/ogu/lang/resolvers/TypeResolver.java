package org.ogu.lang.resolvers;

import org.ogu.lang.definitions.TypeDefinition;

import java.util.Optional;

/**
 * Type Resolver
 * Created by ediaz on 20-01-16.
 */
public interface TypeResolver {

    public TypeResolver root();
    public void setRoot(TypeResolver root);

    public Optional<TypeDefinition> resolveAbsoluteTypeName(String typeName);

    boolean existPackage(String packageName);

    SymbolResolver symbolResolver();
    void setSymbolResolver(SymbolResolver symbolResolver);
}