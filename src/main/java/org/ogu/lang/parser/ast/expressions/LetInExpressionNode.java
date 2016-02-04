package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.HashMap;
import java.util.Map;

/**
 * let id=e, id2=e2,... in expr
 * Created by ediaz on 01-02-16.
 */
public class LetInExpressionNode extends ExpressionNode {

    private Map<IdentifierNode, ExpressionNode> exprs;
    private ExpressionNode inExpr;

    public LetInExpressionNode(Map<IdentifierNode, ExpressionNode> exprs) {
        this.exprs = new HashMap<>();
        this.exprs.putAll(exprs);
        this.exprs.keySet().forEach((k) -> k.setParent(this));
        this.exprs.values().forEach((v) -> v.setParent(this));
    }

    public LetInExpressionNode(Map<IdentifierNode, ExpressionNode> exprs, ExpressionNode inExpr) {
        this.exprs = new HashMap<>();
        this.exprs.putAll(exprs);
        this.exprs.keySet().forEach((k) -> k.setParent(this));
        this.exprs.values().forEach((v) -> v.setParent(this));
        this.inExpr = inExpr;
        this.inExpr.setParent(this);
    }

    @Override
    public String toString() {
        return "LetIn{ decls="+exprs+", in="+inExpr+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        if (inExpr == null)
            return ImmutableList.<Node>builder().addAll(exprs.keySet()).addAll(exprs.values()).build();
        else
            return ImmutableList.<Node>builder().addAll(exprs.keySet()).addAll(exprs.values()).add(inExpr).build();
    }
}
