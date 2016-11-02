package org.ogu.lang.resolvers;

import org.ogu.lang.definitions.TypeDefinition;
import java.util.List;
import java.util.Optional;

/**
 * TODO Implements for real
 * Created by ediaz on 20-01-16.
 */
public class ComposedTypeResolver implements TypeResolver {

    private List<TypeResolver> elements;

    public ComposedTypeResolver(List<TypeResolver> elements) {
        this.elements = elements;
        this.elements.forEach((e)->e.setRoot(ComposedTypeResolver.this));
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
        for (TypeResolver element : elements) {
            Optional<TypeDefinition> partial = element.resolveAbsoluteTypeName(typeName);
            if (partial.isPresent()) {
                return partial;
            }
        }
        return Optional.empty();
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
