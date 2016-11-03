package org.ogu.lang.compiler;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;

import java.util.List;

/**
 * Created by ediaz on 10/31/16.
 */
public class AmbiguousCallException extends RuntimeException {

    private Node context;
    private String name;
    private List<JvmType> actualParamTypes;
    private List<ActualParamNode> actualParams;

    public AmbiguousCallException(Node context, List<ActualParamNode> actualParams, String name) {
        this.context = context;
        this.actualParams = actualParams;
        this.name = name;
    }

    public AmbiguousCallException(Node context, String name, List<JvmType> actualParamTypes) {
        this.context = context;
        this.name = name;
        this.actualParamTypes = actualParamTypes;
    }

    public Node getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public List<JvmType> getActualParamTypes() {
        return actualParamTypes;
    }

    public List<ActualParamNode> getActualParams() {
        return actualParams;
    }

}