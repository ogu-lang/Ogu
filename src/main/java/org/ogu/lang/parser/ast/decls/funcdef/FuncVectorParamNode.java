package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncVectorParamNode extends FunctionPatternParamNode {

    private List<FunctionPatternParamNode> args;


    public FuncVectorParamNode(List<FunctionPatternParamNode> args) {
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
