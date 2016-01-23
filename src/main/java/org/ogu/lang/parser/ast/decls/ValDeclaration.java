package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.typeusage.TypeArg;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class ValDeclaration extends Declaration {

    private Expression value;
    private TypeArg returnType;

    public ValDeclaration(OguIdentifier id, Expression value) {
        super(id);
        this.value = value;
        this.value.setParent(this);
    }

    public ValDeclaration(OguIdentifier id, TypeArg returnType, Expression value) {
        super(id);
        this.returnType = returnType;
        this.returnType.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(id).add(value).build();
    }

    @Override
    public String toString() {
        return "ValDeclaration{" +
                "id='" + id + '\''+
                ", returnType="+returnType+
                ", value=" + value +
                '}';
    }
}
