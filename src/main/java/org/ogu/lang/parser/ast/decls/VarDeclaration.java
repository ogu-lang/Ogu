package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.OguType;
import org.ogu.lang.parser.ast.typeusage.TypeArg;

import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class VarDeclaration extends FunctionalDeclaration {

    protected Expression value;
    protected OguType type;

    protected VarDeclaration(OguType type,  Expression value, List<Decorator> decorators) {
        super(decorators);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    protected VarDeclaration(Expression value, List<Decorator> decorators) {
        super(decorators);
        this.value = value;
        this.value.setParent(this);
    }


    public VarDeclaration(OguIdentifier id, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.value = value;
        this.value.setParent(this);
    }

    public VarDeclaration(OguIdentifier id, OguType type, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return ImmutableList.<Node>builder().add(name).add(value).addAll(decorators).build();
        else
            return ImmutableList.<Node>builder().add(name).add(value).add(type).addAll(decorators).build();
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
