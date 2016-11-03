package org.ogu.lang.parser.analysis.exceptions;

import org.ogu.lang.parser.ast.Node;

/**
 * Created by ediaz on 10/31/16.
 */
public class UnsolvedSymbolException extends UnsolvedException {

    private Node node;

    public UnsolvedSymbolException(Node node) {
        super("No encontrado: " + node);
        this.node = node;
    }

    public UnsolvedSymbolException(String fieldName) {
        super("No encontrado: " + fieldName);
    }

    public UnsolvedSymbolException(Node context, String typeName) {
        super("Símbolo no encontrado "+ typeName + " en "+context.describe());
    }
}