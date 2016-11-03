package org.ogu.lang.resolvers;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

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
    public Optional<TypeDefinition> findTypeDefinitionIn(String typeName, Node context, SymbolResolver resolver) {
        for (SymbolResolver element : elements) {
            Optional<TypeDefinition> definition = element.findTypeDefinitionIn(typeName, context, resolver);
            if (definition.isPresent()) {
                return definition;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TypeDefinition> findTypeDefinitionFromJvmSignature(String jvmSignature, Node context, SymbolResolver resolver) {
        for (SymbolResolver element : elements) {
            Optional<TypeDefinition> definition = element.findTypeDefinitionFromJvmSignature(jvmSignature, context, resolver);
            if (definition.isPresent()) {
                return definition;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<JvmMethodDefinition> findJvmDefinition(FunctionCallNode functionCall) {
        for (SymbolResolver element : elements) {
            Optional<JvmMethodDefinition> partial = element.findJvmDefinition(functionCall);
            if (partial.isPresent()) {
                return partial;
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<Symbol> findSymbol(String name, Node context) {
        for (SymbolResolver element : elements) {
            Optional<Symbol> res = element.findSymbol(name, context);
            if (res.isPresent()) {
                return res;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existPackage(String packageName) {
        for (SymbolResolver element : elements) {
            if (element.existPackage(packageName)) {
                return true;
            }
        }
        return false;
    }


}
