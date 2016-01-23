package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.expressions.Expression;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class ValDeclaration extends Declaration {

    private Expression value;

    public ValDeclaration(OguIdentifier id, Expression value) {
        super(id);
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
                "value=" + value +
                '}';
    }
}
