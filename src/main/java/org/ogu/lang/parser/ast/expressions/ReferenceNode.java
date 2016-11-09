package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.decls.AliasJvmInteropDeclarationNode;
import org.ogu.lang.parser.ast.decls.FreeFunctionDeclarationNode;
import org.ogu.lang.parser.ast.decls.LetDeclarationNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A reference to a type, a val, a var o a function
 * Created by ediaz on 22-01-16.
 */
public class ReferenceNode extends ExpressionNode {
    private IdentifierNode name;

    public ReferenceNode(IdentifierNode name) {
        this.name = name;
        this.name.setParent(this);
    }

    public String getName() {
        return name.getName();
    }

    @Override
    public String toString() {
        return "ReferenceNode{" +
                "name='" + name + '\'' +
                '}';
    }

    private TypeUsageNode precalculatedType;

    @Override
    public TypeUsage calcType() {
        if (precalculatedType != null) {
            return precalculatedType;
        }
        Optional<Symbol> declaration = symbolResolver().findSymbol(getName(), this);

        if (declaration.isPresent()) {
            return declaration.get().calcType();
        } else {
            throw new UnsolvedSymbolException(this);
        }
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(name);
    }


    private Symbol cache;

    public Symbol resolve(SymbolResolver resolver) {
        if (cache != null) {
            return cache;
        }
        Optional<Symbol> declaration = resolver.findSymbol(name.qualifiedName(), this);
        if (declaration.isPresent()) {
            if (!(declaration.get() instanceof Symbol)) {
                throw new UnsupportedOperationException();
            }
            cache = declaration.get();
            return cache;
        } else {
            throw new UnsolvedSymbolException(this);
        }
    }

    @Override
    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invocable) {
        return resolve(symbolResolver()).findFormalParametersFor(invocable);
    }

    @Override
    public JvmMethodDefinition findFunctionFor(List<ActualParamNode> actualParamNodes, SymbolResolver resolver) {
        List<JvmType> argsTypes = actualParamNodes.stream().map((ap) -> ap.getValue().calcType().jvmType()).collect(Collectors.toList());
        Optional<Symbol> declaration = resolver.findSymbol(name.getName(), this);
        if (declaration.isPresent()) {
            Symbol decl = declaration.get();

            if (decl instanceof ExpressionNode) {
                Logger.debug("DEBERIA PASAR POR ACA EXPR=" + decl);
                return ((ExpressionNode) decl).findFunctionFor(actualParamNodes, resolver);
            } else if (decl instanceof AliasJvmInteropDeclarationNode) {
                return ((AliasJvmInteropDeclarationNode) decl).findFunctionFor(actualParamNodes, resolver);
            } else if (decl instanceof LetDeclarationNode) {
                LetDeclarationNode letDecl = (LetDeclarationNode) decl;
                if (letDecl.match(argsTypes, resolver)) {
                    return letDecl.jvmMethodDefinition(resolver);
                } else {
                    Logger.debug("PARECE QUE FALLA EL MATCH");
                    throw new IllegalArgumentException();
                }
            } else if (decl instanceof FreeFunctionDeclarationNode) {
                FreeFunctionDeclarationNode freeDecl = (FreeFunctionDeclarationNode) decl;
                return freeDecl.jvmMethodDefinition(argsTypes, resolver);
            }else {
                throw new UnsupportedOperationException(declaration.get().getClass().getCanonicalName());
            }
        }
        throw new UnsolvedSymbolException(this);
    }
}
