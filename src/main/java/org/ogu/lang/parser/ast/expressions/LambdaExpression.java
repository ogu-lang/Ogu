package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.control.DoExpression;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * \args -> expr
 * Created by ediaz on 01-02-16.
 */
public class LambdaExpression extends Expression {

    private List<LambdaArg> args;
    private DoExpression doExpr;

    public LambdaExpression(List<LambdaArg> args, DoExpression expr) {
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.doExpr = expr;
        this.doExpr.setParent(this);
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(args).add(doExpr).build();
    }


}
