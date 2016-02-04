package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * A set constraint is the part after |
 * [a | a <- 0..10] ; a<-0..10
 * [(a,b) | a <- 0..10 | b <- 0..10]
 * [imc p a | (p, a) <- lista]
 * Created by ediaz on 31-01-16.
 */
public class SetConstraint extends ExpressionNode {

    List<IdentifierNode> ids;
    ExpressionNode expressionNode;

    public SetConstraint(List<IdentifierNode> ids, ExpressionNode expressionNode) {
        super();
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
        this.expressionNode = expressionNode;
        this.expressionNode.setParent(this);
    }

    public SetConstraint(IdentifierNode id, ExpressionNode expressionNode) {
        this(ImmutableList.of(id), expressionNode);
    }

    @Override
    public String toString() {
        return "SetConstraint {" +
                "ids = "+ids +
                ", expression = "+ expressionNode +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ids).add(expressionNode).build();
    }

}
