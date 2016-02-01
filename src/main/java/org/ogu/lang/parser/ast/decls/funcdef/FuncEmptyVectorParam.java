package org.ogu.lang.parser.ast.decls.funcdef;

import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncEmptyVectorParam extends FunctionPatternParam {


    public FuncEmptyVectorParam() {
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
}
