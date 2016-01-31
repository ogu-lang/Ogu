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
    RangeExpression range;

    public SetConstraint(List<OguIdentifier> ids, RangeExpression range) {
        super();
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
        this.range = range;
        this.range.setParent(this);
    }

    public SetConstraint(OguIdentifier id, RangeExpression range) {
        this(ImmutableList.of(id), range);
    }

    @Override
    public String toString() {
        return "SetConstraint {" +
                "ids = "+ids +
                ", range = "+range+
                '}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ids).add(range).build();
    }

}
