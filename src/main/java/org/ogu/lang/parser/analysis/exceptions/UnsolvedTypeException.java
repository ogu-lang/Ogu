package org.ogu.lang.parser.analysis.exceptions;

import org.ogu.lang.parser.ast.Node;

/**
 * Created by ediaz on 11/5/16.
 */
public class UnsolvedTypeException extends UnsolvedException {

    public UnsolvedTypeException(String typeName, Node context) {
        super("type " + typeName + " not solved in " + context);
    }
}
