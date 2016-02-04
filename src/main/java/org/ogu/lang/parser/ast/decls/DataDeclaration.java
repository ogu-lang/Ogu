package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * data Data I J = A I | B J | ...
 * Created by ediaz on 29-01-16.
 */
public class DataDeclaration extends AlgebraicDataTypeDeclaration {

    private List<TypeParam> params;
    private List<TypeNode> values;

    public DataDeclaration(TypeIdentifierNode name, List<TypeParam> params, List<TypeNode> values, List<TypeIdentifierNode> deriving, List<Decorator> decorators) {
        super(name, deriving, decorators);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.values.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(params)
                .addAll(values)
                .addAll(deriving)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "Enum{" +
                "name='" + name + '\''+
                ", params=" + params +
                ", values=" + values +
                ", deriving=" + deriving +
                ", decorators=" + decorators +
                '}';
    }
}
