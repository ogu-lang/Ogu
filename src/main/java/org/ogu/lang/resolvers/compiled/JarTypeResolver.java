package org.ogu.lang.resolvers.compiled;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.invocables.FunctionDefinitionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.TypeResolver;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * TODO: Implement
 * Created by ediaz on 20-01-16.
 */
public class JarTypeResolver implements TypeResolver {

    public JarTypeResolver(File file) throws IOException {
    }

    @Override
    public TypeResolver root() {
        return null;
    }

    @Override
    public void setRoot(TypeResolver root) {

    }

    @Override
    public Optional<TypeDefinition> resolveAbsoluteTypeName(String typeName) {
        return null;
    }

    @Override
    public Optional<FunctionDefinitionNode> resolveAbsoluteFunctionName(String typeName) {
        return null;
    }

    @Override
    public boolean existPackage(String packageName) {
        return false;
    }

    @Override
    public SymbolResolver symbolResolver() {
        return null;
    }

    @Override
    public void setSymbolResolver(SymbolResolver symbolResolver) {

    }
}
