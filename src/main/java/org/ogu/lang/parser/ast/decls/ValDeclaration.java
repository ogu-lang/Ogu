package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class ValDeclaration extends FunctionalDeclaration {

    private Expression value;
    private OguType returnType;

    public ValDeclaration(OguIdentifier id, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.value = value;
        this.value.setParent(this);
    }

    public ValDeclaration(OguIdentifier id, OguType returnType, Expression value, List<Decorator> decorators) {
        super(id, decorators);
        this.returnType = returnType;
        this.returnType.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).add(value).addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "ValDeclaration{" +
                "id='" + name + '\''+
                ", returnType="+returnType+
                ", value=" + value +
                ", decorators" + decorators +
                '}';
    }



}
