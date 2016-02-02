package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.Collections;

/**
 * Try to negate a string or date literal
 * Created by ediaz on 30-01-16.
 */
public class NegExpressionError extends NegExpression {

    private String message;

    public NegExpressionError(String message, Position position) {
        super(null);
        this.message = message;
        this.setPosition(position);
    }

    @Override
    protected boolean specificValidate(SymbolResolver resolver, ErrorCollector errorCollector) {
        errorCollector.recordSemanticError(getPosition(), message);
        return false;
    }


    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
