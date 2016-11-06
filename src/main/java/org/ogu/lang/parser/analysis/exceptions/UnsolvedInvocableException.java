package org.ogu.lang.parser.analysis.exceptions;

import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;

/**
 * Created by ediaz on 10/30/16.
 */
public class UnsolvedInvocableException extends UnsolvedException {


    public UnsolvedInvocableException(InvocableExpressionNode invokable) {
        super("No se puede encontrar expresión para invocar " + invokable.toString());
    }
}
