package org.ogu.lang.symbols;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.QualifiedName;

/**
 * Created by ediaz on 20-01-16.
 */
public interface Symbol {

    //TypeUsage calcType();

    /**
     * Is this symbol an AST node?
     */
    default boolean isNode() {
        return false;
    }

    default Node asNode() {
        throw new UnsupportedOperationException();
    }

    default Symbol getField(String fieldName) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    default Symbol getField(QualifiedName fieldsPath) {
        if (fieldsPath.isSimpleName()) {
            return getField(fieldsPath.getName());
        } else {
            Symbol next = getField(fieldsPath.firstSegment());
            return next.getField(fieldsPath.rest());
        }
    }

    //default Optional<List<? extends FormalParameter>> findFormalParametersFor(InvokableExpr invokable) {
    //    throw new UnsupportedOperationException();
    //}
}
