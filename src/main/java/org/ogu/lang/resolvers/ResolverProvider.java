package org.ogu.lang.resolvers;


import org.ogu.lang.parser.ast.Node;

import java.util.Optional;

/**
 * Created by ediaz on 21-01-16.
 */
public interface ResolverProvider {
    Optional<SymbolResolver> findResolver(Node node);

    SymbolResolver requireResolver(Node node);
}
