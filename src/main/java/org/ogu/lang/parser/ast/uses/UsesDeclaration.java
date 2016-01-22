package org.ogu.lang.parser.ast.uses;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;

import java.util.Optional;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

public abstract class UsesDeclaration extends Node {

    public abstract Optional<Symbol> findAmongImported(String name, SymbolResolver resolver);
}