package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.Symbol;

import java.util.Optional;

/**
 * Created by ediaz on 20-01-16.
 */
public interface SymbolResolver {

    SymbolResolver getParent();

    void setParent(SymbolResolver parent);

    default SymbolResolver getRoot() {
        if (getParent() == null)
            return this;
        return getParent().getRoot();
    }

    Optional<Symbol> findSymbol(String name, Node context);

}
