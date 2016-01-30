package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.ArrayList;
import java.util.List;

/**
 * data Data I J = A I | B J | ...
 * Created by ediaz on 29-01-16.
 */
public class DataDeclaration extends AlgebraicDataTypeDeclaration {

    private List<TypeParam> params;
    private List<OguType> values;

    public DataDeclaration(OguTypeIdentifier name, List<TypeParam> params, List<OguType> values, List<OguTypeIdentifier> deriving, List<Decorator> decorators) {
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
