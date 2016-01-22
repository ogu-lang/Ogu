package org.ogu.lang.resolvers;

/**
 * Created by ediaz on 20-01-16.
 */
public interface SymbolResolver {

    public SymbolResolver getParent();

    public void setParent(SymbolResolver parent);
}
