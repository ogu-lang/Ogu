package org.ogu.lang.resolvers;

import java.util.ArrayList;
import java.util.List;

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
}
