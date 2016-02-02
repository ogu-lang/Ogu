package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.compiler.Ogu;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class TupleValDeclaration extends ValDeclaration {

    private List<OguIdentifier> ids;
    private Map<OguIdentifier, OguType> types;

    public TupleValDeclaration(List<OguIdentifier> ids,  Map<OguIdentifier, OguType> types, Expression value, List<Decorator> decorators) {
        super(value, decorators);
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
        this.types = new HashMap<>();
        this.types.putAll(types);
        this.types.keySet().forEach((k) -> k.setParent(this));
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ids).addAll(types.values()).add(value).addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "TupleValDeclaration{" +
                "ids='" + ids + '\''+
                ", types="+types+
                ", value=" + value +
                ", decorators" + decorators +
                '}';
    }



}
