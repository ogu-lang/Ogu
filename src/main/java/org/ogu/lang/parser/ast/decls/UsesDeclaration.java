package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;

import java.util.List;
import java.util.Optional;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

public class UsesDeclaration extends NameDeclaration {



    @Override
    public String toString() {
        return "UsesDeclaration{" +
                "name=" + name +
                '}';
    }

    public UsesDeclaration(TypeIdentifierNode name, List<Decorator> decorators) {
        super(name, decorators);
    }

    public  Optional<Symbol> findAmongImported(String name, SymbolResolver resolver) {
        // TODO implement search
        return null;
    }


}