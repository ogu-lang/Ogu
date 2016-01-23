package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.Collections;

/**
 * A semantic error on Alias declaration
 * Created by ediaz on 22-01-16.
 */
public class AliasError extends AliasDeclaration {

    private String message;
    private Position position;

    public AliasError(String message, Position position) {
        this.message = message;
        this.position = position;
    }

    @Override
    protected boolean specificValidate(SymbolResolver resolver, ErrorCollector errorCollector) {
        errorCollector.recordSemanticError(position, message);
        return false;
    }


    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

}
