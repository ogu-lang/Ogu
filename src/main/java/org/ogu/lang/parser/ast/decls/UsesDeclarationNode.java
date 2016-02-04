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

public class UsesDeclarationNode extends NameDeclarationNode {



    @Override
    public String toString() {
        return "UsesDeclaration{" +
                "name=" + name +
                '}';
    }

    public UsesDeclarationNode(TypeIdentifierNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }

    public  Optional<Symbol> findAmongImported(String name, SymbolResolver resolver) {
        // TODO implement search
        return null;
    }


}