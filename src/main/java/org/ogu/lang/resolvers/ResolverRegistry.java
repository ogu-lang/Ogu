package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 21-01-16.
 */

public enum ResolverRegistry implements ResolverProvider {

    INSTANCE;

    public void record(Node node, SymbolResolver resolver) {
        if (!node.isRoot()) {
            throw new IllegalArgumentException();
        }
        resolvers.put(node, resolver);
    }

    @Override
    public Optional<SymbolResolver> findResolver(Node node) {
        Node root = node.getRoot();
        if (resolvers.containsKey(root)) {
            return Optional.of(resolvers.get(root));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public SymbolResolver requireResolver(Node node) {
        Optional<SymbolResolver> or = findResolver(node);
        if (or.isPresent()) {
            return or.get();
        } else {
            throw new IllegalStateException(node.toString());
        }
    }

    private Map<Node, SymbolResolver> resolvers = new IdentityHashMap<>();

}