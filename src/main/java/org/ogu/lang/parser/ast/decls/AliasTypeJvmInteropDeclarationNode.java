package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;
import org.omg.PortableServer.CurrentPackage.NoContext;

import java.util.List;
import java.util.Optional;

/**
 * Created by ediaz on 11/3/16.
 */
public class AliasTypeJvmInteropDeclarationNode extends AliasDeclarationNode {

    private String jvmSignature;
    protected List<DecoratorNode> decoratorNodes;

    public AliasTypeJvmInteropDeclarationNode(TypeIdentifierNode target, String signature, List<DecoratorNode> decoratorNodes) {
         super(target, decoratorNodes);
         jvmSignature = signature.replaceAll("\"", "");
    }


    private Optional<TypeDefinition> typeDefinitionCache;


    private void findTypeDefinition(SymbolResolver resolver) {
        if (typeDefinitionCache != null) {
            return;
        }
        typeDefinitionCache = resolver.findTypeDefinitionFromJvmSignature(jvmSignature, this, resolver);
    }

    public Optional<Symbol> findAmongImported(String name, SymbolResolver resolver) {
        if (this.name.getName().equals(name)) {
            findTypeDefinition(resolver);
            if (typeDefinitionCache.isPresent()) {
                return Optional.of(typeDefinitionCache.get());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "AliasTypeJvmInteropDeclarationNode{" +
                "name=" + name +
                ", signature=" + jvmSignature +
                "decorators='" + decoratorNodes + "\'}";
    }

    @Override
    public Iterable<Node> getChildren() {
        if (decoratorNodes != null) {
            return ImmutableList.<Node>builder()
                    .add(name)
                    .addAll(decoratorNodes).build();
        } else {
            return ImmutableList.of(name);
        }

    }

    @Override
    public String getName() {
        return name.getName();
    }
}
