package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO Implements for real
 * Created by ediaz on 20-01-16.
 */
public class ComposedSymbolResolver  implements SymbolResolver {

    private List<SymbolResolver> elements = new ArrayList<>();

    public ComposedSymbolResolver(List<SymbolResolver> elements) {
        this.elements = elements;
        this.elements.forEach((e)->e.setParent(ComposedSymbolResolver.this));
    }

    private SymbolResolver parent = null;

    @Override
    public SymbolResolver getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolResolver parent) {
        this.parent = parent;
    }

    @Override
    public Optional<Symbol> findSymbol(String name, Node context) {
        return null;
    }
}
