package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Map;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncExprParamNode extends FunctionPatternParamNode {

    private ExpressionNode expr;


    public FuncExprParamNode(ExpressionNode expr) {
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

    @Override
    public TypeUsage getType() {
        return expr.calcType();
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
