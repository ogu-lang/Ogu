package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.ArrayList;
import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class TupleVarDeclaration extends VarDeclaration {


    private List<IdentifierNode> ids;

    public TupleVarDeclaration(List<IdentifierNode> ids, Expression value, List<Decorator> decorators) {
        super(value, decorators);
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
    }

    public TupleVarDeclaration(List<IdentifierNode> ids, OguType type, Expression value, List<Decorator> decorators) {
        super(type, value, decorators);
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
    }


    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return ImmutableList.<Node>builder().addAll(ids).add(value).addAll(decorators).build();
        else
            return ImmutableList.<Node>builder().addAll(ids).add(type).add(value).addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "TupleVarDeclaration{" +
                "ids='" + ids + '\''+
                ", i="+type+
                ", value=" + value +
                ", decorators" + decorators +
                '}';
    }

}
