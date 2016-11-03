package org.ogu.lang.parser.analysis.exceptions;

import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;

import java.util.List;

/**
 * Created by ediaz on 10/31/16.
 */
public class UnsolvedFunctionException extends UnsolvedException {

    public UnsolvedFunctionException(FunctionCallNode functionCallNode) {
        super("Llamada a función no resuelta "+functionCallNode.toString());
    }

    public UnsolvedFunctionException(String qualifiedName, String functionName, List<ActualParamNode> actualParams) {
        super("Función no encontrada: " + functionName);
    }
}
