package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.ArrayList;
import java.util.List;

/**
 * A type declared like
 *     type T a b c = ...
 * (Useful to restrict a data)
 * Created by ediaz on 24-01-16.
 */
public class GenericTypeDeclaration  extends TypedefDeclaration {

    protected List<TypeParam> params;

    public GenericTypeDeclaration(OguTypeIdentifier name, List<TypeParam> params, OguType type, List<Decorator> decorators) {
        super(name, type, decorators);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



    @Override
    public String toString() {
        return "GenericTypeDeclaration{"+
                "name="+name+
                ", type="+type+
                ", params="+params+
                ", decorators="+decorators+
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .add(type)
                .addAll(params)
                .addAll(decorators).build();
    }
}
