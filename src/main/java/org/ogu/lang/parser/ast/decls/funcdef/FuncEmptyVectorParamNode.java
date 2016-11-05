package org.ogu.lang.parser.ast.decls.funcdef;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;
import java.util.Map;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncEmptyVectorParamNode extends FunctionPatternParamNode {


    public FuncEmptyVectorParamNode() {
        super();
    }

    @Override
    public String toString() {
        return "FuncEmptyVectorParam{}";
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public TypeUsage getType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public FormalParameter apply(Map<String, TypeUsage> typeParams) {
        return null;
    }
}
