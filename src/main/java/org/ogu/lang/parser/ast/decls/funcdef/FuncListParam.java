package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncListParam extends FunctionPatternParam {

    private List<FunctionPatternParam> params;


    public FuncListParam(List<FunctionPatternParam> params) {
        super();
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }

    @Override
    public String toString() {
        return "FuncListParam{"+
                params+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(params).build();
    }
}
