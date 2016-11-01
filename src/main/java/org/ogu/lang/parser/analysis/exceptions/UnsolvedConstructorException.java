package org.ogu.lang.parser.analysis.exceptions;

import org.ogu.lang.parser.ast.expressions.ActualParamNode;

import java.util.List;

/**
 * Created by ediaz on 10/31/16.
 */
public class UnsolvedConstructorException extends UnsolvedException {

    private String typeCanonicalName;
    private List<ActualParamNode> paramList;

    public UnsolvedConstructorException(String typeCanonicalName, List<ActualParamNode> paramList) {
        super("Unsolved constructor for " + typeCanonicalName + " with params " + paramList);
        this.typeCanonicalName = typeCanonicalName;
        this.paramList = paramList;
    }

    public UnsolvedConstructorException(String typeCanonicalName, List<ActualParamNode> paramList, String detail) {
        super("Unsolved constructor for " + typeCanonicalName + " with params " + paramList + ": " + detail);
        this.typeCanonicalName = typeCanonicalName;
        this.paramList = paramList;
    }
}