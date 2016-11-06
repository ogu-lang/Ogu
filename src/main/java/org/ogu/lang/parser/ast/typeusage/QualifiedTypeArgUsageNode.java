package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.resolvers.ResolverRegistry;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.Optional;


/**
 * A type like M1.M2.T
 * Created by ediaz on 23-01-16.
 */
public class QualifiedTypeArgUsageNode extends TypeArgUsageWrapperNode {

    private TypeIdentifierNode typeId;

    public QualifiedTypeArgUsageNode(TypeIdentifierNode typeId) {
        this.typeId = typeId;
        this.typeId.setParent(this);
    }


    @Override
    public TypeUsage typeUsage() {
        if (typeUsage == null) {
            SymbolResolver resolver = ResolverRegistry.INSTANCE.requireResolver(this);
            typeUsage = new ReferenceTypeUsage(getTypeDefinition(resolver));
        }
        return super.typeUsage();
    }

    public boolean isInterface(SymbolResolver resolver) {
        return getTypeDefinition(resolver).isInterface();
    }

    public boolean isClass(SymbolResolver resolver) {
        return getTypeDefinition(resolver).isClass();
    }

    public boolean isEnum(SymbolResolver resolver) {
        throw new UnsupportedOperationException();
    }

    public boolean isTypeVariable(SymbolResolver resolver) {
        throw new UnsupportedOperationException();
    }


    private TypeDefinition cachedTypeDefinition;

    public TypeDefinition getTypeDefinition(SymbolResolver resolver) {
        if (cachedTypeDefinition != null) {
            return cachedTypeDefinition;
        }
        TypeDefinition typeDefinition = resolver.getRoot().getTypeDefinitionIn(this.typeId.qualifiedName(), this);
        return typeDefinition;
    }


    @Override
    public String toString() {
        return "QualifiedTypeArgUsageNode{"
            +"typeid="+typeId +'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(typeId);
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

    @Override
    public TypeUsage calcType() {
        Optional<Symbol> declaration = symbolResolver().findSymbol(typeId.qualifiedName(), this);
        if (declaration.isPresent()) {
            return declaration.get().calcType();
        } else {
            throw new UnsolvedSymbolException(this);
        }
    }

    @Override
    public TypeUsageNode copy() {
        QualifiedTypeArgUsageNode copy = new QualifiedTypeArgUsageNode(typeId);
        copy.parent = this.parent;
        copy.cachedTypeDefinition = this.cachedTypeDefinition;
        return copy;
    }
}
