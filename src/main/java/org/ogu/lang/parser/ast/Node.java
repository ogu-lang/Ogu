package org.ogu.lang.parser.ast;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.resolvers.ResolverRegistry;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;
import java.util.Optional;

/**
 * An AST Node
 * Created by ediaz on 20-01-16.
 */
public abstract class Node implements Symbol {

    protected Node parent;
    private Position position;
    private Boolean valid;

    public abstract Iterable<Node> getChildren();

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public Node asNode() {
        return this;
    }

    public final boolean validate(SymbolResolver resolver, ErrorCollector errorCollector) {
        boolean res = specificValidate(resolver, errorCollector);
        // if the node is wrong we do not check its children
        if (res) {
            for (Node child : getChildren()) {
                boolean partial = child.validate(resolver, errorCollector);
                if (!partial) {
                    res = false;
                }
            }
        }
        valid = res;
        return res;
    }

    protected SymbolResolver symbolResolver() {
        return ResolverRegistry.INSTANCE.requireResolver(this);
    }

    public Position getPosition() {
        if (position == null) {
            throw new IllegalStateException(this.toString()+ " has no position assigned");
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invocable) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    public Node getRoot() {
        if (isRoot()) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }


    public boolean isRoot() {
        return parent == null;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        if (parent == this) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
    }

    public String contextName() {
        if (parent == null) {
            return "";
        }
        if (parent instanceof ModuleNode) {
            ModuleNode module = (ModuleNode)parent;
            return module.getNameDefinition().getName();
        }
        return parent.contextName();
    }

    public boolean isValid() {
        if (valid == null) {
            throw new IllegalStateException("Not validated");
        }
        return valid;
    }

    @Override
    public TypeUsage calcType() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    /**
     * @return if the node is valid
     */
    protected boolean specificValidate(SymbolResolver resolver, ErrorCollector errorCollector) {
        // nothing to do
        return true;
    }

    public String describe() {
        return this.toString();
    }
}
