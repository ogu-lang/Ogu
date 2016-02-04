package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class ValDeclaration extends FunctionalDeclaration {

    protected Expression value;
    protected OguType type;

    protected ValDeclaration(OguType type,  Expression value, List<Decorator> decorators) {
        super(decorators);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    protected ValDeclaration(Expression value, List<Decorator> decorators) {
        super(decorators);
        this.value = value;
        this.value.setParent(this);
    }


    public ValDeclaration(IdentifierNode id, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.value = value;
        this.value.setParent(this);
    }

    public ValDeclaration(IdentifierNode id, OguType returnType, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.type = returnType;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return ImmutableList.<Node>builder().add(name).add(value).addAll(decorators).build();
        else
            return ImmutableList.<Node>builder().add(name).add(type).add(value).addAll(decorators).build();

    }

    @Override
    public String toString() {
        return "ValDeclaration{" +
                "id='" + name + '\''+
                ", type="+type+
                ", value=" + value +
                ", decorators" + decorators +
                '}';
    }



}
