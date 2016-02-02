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
public class FuncGenericTypeParam extends FunctionPatternParam {

    private OguTypeIdentifier name;
    private List<FunctionPatternParam> args;


    public FuncGenericTypeParam(OguTypeIdentifier name, List<FunctionPatternParam> args) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((p) -> p.setParent(this));
    }

    @Override
    public String toString() {
        return "FuncGenericTypeParam{"+
                "name="+name+
                ", args="+args+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).build();
    }
}
