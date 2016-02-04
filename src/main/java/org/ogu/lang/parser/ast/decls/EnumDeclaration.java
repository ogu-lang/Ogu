package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.TypeIdentifierNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 28-01-16.
 */
public class EnumDeclaration extends AlgebraicDataTypeDeclaration {

    private List<IdentifierNode> values;

    public EnumDeclaration(TypeIdentifierNode name, List<IdentifierNode> values, List<TypeIdentifierNode> deriving, List<Decorator> decorators) {
        super(name, deriving, decorators);
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(values)
                .addAll(deriving)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "Enum{" +
                "name='" + name + '\''+
                ", values=" + values +
                ", deriving=" + deriving +
                ", decorators=" + decorators +
                '}';
    }

}
