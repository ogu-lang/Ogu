package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.control.DoExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 01-02-16.
 */
public class GuardDeclarationNode extends FunctionNode {

    private ExpressionNode base;
    private List<ExpressionNode> args;
    private DoExpressionNode block;

    public GuardDeclarationNode(ExpressionNode base, List<ExpressionNode> args, DoExpressionNode block) {
        super();
        this.base = base;
        this.base.setParent(this);
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.block = block;
        this.block.setParent(this);
    }


    @Override
    public String toString() {
        return "Guard {"+
                "expr = "+base+
                ", args = "+args+
                ", do = "+block+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(base).addAll(args).add(block).build();
    }
}
