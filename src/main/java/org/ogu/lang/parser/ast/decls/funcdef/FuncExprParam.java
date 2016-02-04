package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncExprParam extends FunctionPatternParam {

    private Expression expr;


    public FuncExprParam(Expression expr) {
        super();
        this.expr = expr;
        this.expr.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncExprParam{"+
                "expr="+expr+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(expr);
    }
}
