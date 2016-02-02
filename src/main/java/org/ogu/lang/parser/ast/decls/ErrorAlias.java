package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.parser.ast.decls.AliasDeclaration;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.Collections;
import java.util.List;

/**
 * A semantic error on Alias declaration
 * Created by ediaz on 22-01-16.
 */
public class ErrorAlias extends AliasDeclaration {

    private String message;

    public ErrorAlias(String message, Position position) {
        super(Collections.emptyList());
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
