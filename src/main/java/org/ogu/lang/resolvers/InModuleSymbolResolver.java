package org.ogu.lang.resolvers;

/**
 * TODO: Complete
 * Created by ediaz on 20-01-16.
 */
public class InModuleSymbolResolver implements SymbolResolver {


    private TypeResolver typeResolver;

    public InModuleSymbolResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.typeResolver.setSymbolResolver(this);
    }

    @Override
    public SymbolResolver getParent() {
        return null;
    }

    @Override
    public void setParent(SymbolResolver parent) {

    }
}
