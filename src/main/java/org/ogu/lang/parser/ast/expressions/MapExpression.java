package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map : {a->b, c->d}
 * Created by ediaz on 01-02-16.
 */
public class MapExpression extends DictExpression {

    private List<Expression> keys;
    private List<Expression> vals;

    public MapExpression(List<Expression> keys, List<Expression> vals) {
        super();
        this.keys = new ArrayList<>();
        this.keys.addAll(keys);
        this.keys.forEach((k) -> k.setParent(this));
        this.vals = new ArrayList<>();
        this.vals.addAll(vals);
        this.vals.forEach((v) -> v.setParent(this));
    }

    @Override
    public String toString() {
        return "Map {keys="+keys+", vals="+vals+'}';
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(keys).addAll(vals).build();
    }
}
