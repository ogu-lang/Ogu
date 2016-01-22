package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReflectionBasedTypeDefinition implements TypeDefinition {


    private Class<?> clazz;
    private List<TypeUsage> typeParameters = new LinkedList<>();
    private SymbolResolver resolver;

    public ReflectionBasedTypeDefinition(Class<?> clazz, SymbolResolver resolver) {
        if (!clazz.getCanonicalName().startsWith("java.") && !clazz.getCanonicalName().startsWith("javax.")) {
            throw new IllegalArgumentException(clazz.getCanonicalName());
        }
        this.clazz = clazz;
        this.resolver = resolver;
    }

    public void addTypeParameter(TypeUsage typeUsage) {
        typeParameters.add(typeUsage);
    }

    @Override
    public String getName() {
        return null;
    }
}
