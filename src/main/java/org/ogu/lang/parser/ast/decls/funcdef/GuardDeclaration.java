package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.expressions.control.DoExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 01-02-16.
 */
public class GuardDeclaration extends FunctionNode {

    private Expression base;
    private List<Expression> args;
    private DoExpression block;

    public GuardDeclaration(Expression base, List<Expression> args, DoExpression block) {
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
