package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Self references using $
 * Created by ediaz on 02-02-16.
 */
public class SelfRefExpressionNode extends ExpressionNode {

    private IdentifierNode id;


    public SelfRefExpressionNode(IdentifierNode id) {
        super();
        this.id = id;
        this.id.setParent(this);
    }


    @Override
    public String toString() {
        return "SelfRefExpr{"+
                "id="+id+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(id);
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }
}
