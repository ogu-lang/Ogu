package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.TypeResolver;
import java.util.Optional;

/**
 * Created by ediaz on 20-01-16.
 */
public class JdkTypeResolver implements TypeResolver {

    private static JdkTypeResolver INSTANCE = new JdkTypeResolver();

    private JdkTypeResolver() {

    }

    public static JdkTypeResolver getInstance() {
        return INSTANCE;
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
        if (!JvmNameUtils.isValidQualifiedName(typeName)) {
            throw new IllegalArgumentException(typeName);
        }
        return ReflectionTypeDefinitionFactory.getInstance().findTypeDefinition(typeName, symbolResolver());
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
