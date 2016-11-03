package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.Collections;

/**
 * Error when declares var
 * Created by ediaz on 06-02-16.
 */
public class ErrorValDeclarationNode extends ValDeclarationNode {

    private String message;

    public ErrorValDeclarationNode(String message, Position position) {
        super();
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
