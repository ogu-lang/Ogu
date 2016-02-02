package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.ClassParam;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 25-01-16.
 */
public class ClassDeclaration extends ContractDeclaration {

    private List<TypeParam> genParams;
    private List<ClassParam> clasParams;
    private boolean isMutable;

    public ClassDeclaration(OguTypeIdentifier name, boolean isMutable, List<TypeParam> genParams, List<ClassParam> classParams, List<FunctionalDeclaration> members, List<Decorator> decorators) {
        super(name, members, decorators);
        this.isMutable = isMutable;
        this.genParams = new ArrayList<>();
        this.genParams.addAll(genParams);
        this.genParams.forEach((p) -> p.setParent(this));
        this.clasParams = new ArrayList<>();
        this.clasParams = classParams;
        this.clasParams.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(genParams)
                .addAll(clasParams)
                .addAll(members)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "ClassDeclaration{" +
                "name='" + name + '\''+
                ", isMutable=" + isMutable +
                ", genParams=" + genParams+
                ", params=" + clasParams+
                ", members=" + members +
                ", decorators=" + decorators +
                '}';
    }

}


