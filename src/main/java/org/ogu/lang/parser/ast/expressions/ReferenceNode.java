package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Optional;

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

    @Override
    public String toString() {

        return "Reference{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        Optional<Symbol> declaration = symbolResolver().findSymbol(name.qualifiedName(), this);

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
        /*
        if (invocable instanceof FunctionCallNode) {
            Logger.debug("name("+name+").calcType() = "+name.calcType());
            return name.calcType().asInvocable().internalInvocableDefinitionFor(invocable.getActualParamNodes()).map(
                    (iid) -> iid.getFormalParameters());
        } else {
            throw new UnsupportedOperationException(invocable.getClass().getCanonicalName());
        }
        */
    }
}
