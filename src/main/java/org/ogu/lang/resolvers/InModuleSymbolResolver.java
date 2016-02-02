package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.Symbol;

import java.util.Optional;

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

    @Override
    public Optional<Symbol> findSymbol(String name, Node context) {
        return null;
    }
}
