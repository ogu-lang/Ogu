package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;
import java.util.List;

/**
 * Any Declaration
 * Created by ediaz on 23-01-16.
 */
public abstract class NameDeclaration extends Declaration {

    protected OguName name;

    protected NameDeclaration(List<Decorator> decorators) {
        super(decorators);
    }

    protected NameDeclaration(OguName name, List<Decorator> decorators) {
        super(decorators);
        this.name = name;
        this.name.setParent(this);
    }

    public String getName() {
        if (name == null)
            return null;
        return name.getName();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "name='" + name + '\''+
                ", decorators=" + decorators +
                '}';
    }
}
