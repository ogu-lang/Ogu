package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
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
public class SetConstraint extends Expression {

    List<OguIdentifier> ids;
    Expression expression;

    public SetConstraint(List<OguIdentifier> ids, Expression expression) {
        super();
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
        this.expression = expression;
        this.expression.setParent(this);
    }

    public SetConstraint(OguIdentifier id, Expression expression) {
        this(ImmutableList.of(id), expression);
    }

    @Override
    public String toString() {
        return "SetConstraint {" +
                "ids = "+ids +
                ", expression = "+ expression +
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ids).add(expression).build();
    }

}
