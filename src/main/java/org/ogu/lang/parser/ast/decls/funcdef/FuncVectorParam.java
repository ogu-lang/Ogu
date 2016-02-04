package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncVectorParam extends FunctionPatternParam {

    private List<FunctionPatternParam> args;


    public FuncVectorParam(List<FunctionPatternParam> args) {
        super();
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
    }

    @Override
    public String toString() {
        return "FuncVectorParam{"+
                "args="+args+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).build();
    }

}
