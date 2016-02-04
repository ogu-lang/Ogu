package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class TupleVarDeclarationNode extends VarDeclarationNode {


    private List<IdentifierNode> ids;

    public TupleVarDeclarationNode(List<IdentifierNode> ids, ExpressionNode value, List<Decorator> decorators) {
        super(value, decorators);
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
    }

    public TupleVarDeclarationNode(List<IdentifierNode> ids, TypeNode type, ExpressionNode value, List<Decorator> decorators) {
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
