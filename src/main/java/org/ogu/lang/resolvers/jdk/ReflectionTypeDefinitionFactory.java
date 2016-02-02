package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;
import java.util.List;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReflectionTypeDefinitionFactory {

    private static final ReflectionTypeDefinitionFactory INSTANCE = new ReflectionTypeDefinitionFactory();

    public static ReflectionTypeDefinitionFactory getInstance() {
        return INSTANCE;
    }

    public TypeDefinition getTypeDefinition(Class<?> clazz, SymbolResolver resolver) {
        return getTypeDefinition(clazz, Collections.emptyList(), resolver);
    }

    public TypeDefinition getTypeDefinition(Class<?> clazz, List<TypeUsage> typeParams, SymbolResolver resolver) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException();
        }
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException();
        }
        ReflectionBasedTypeDefinition type = new ReflectionBasedTypeDefinition(clazz, resolver);
        for (TypeUsage typeUsage : typeParams) {
            type.addTypeParameter(typeUsage);
        }
        return type;
    }
}
