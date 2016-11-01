package org.ogu.lang.resolvers;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
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

    default TypeDefinition getTypeDefinitionIn(String typeName, Node context) {
        SymbolResolver resolver = ResolverRegistry.INSTANCE.requireResolver(context);
        Optional<TypeDefinition> result = findTypeDefinitionIn(typeName, context, resolver.getRoot());
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new UnsolvedSymbolException(context, typeName);
        }
    }

    Optional<TypeDefinition> findTypeDefinitionIn(String typeName, Node context, SymbolResolver resolver);

    Optional<JvmMethodDefinition> findJvmDefinition(FunctionCallNode functionCall);

    boolean existPackage(String packageName);


}
