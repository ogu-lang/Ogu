package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;

import java.util.ArrayList;
import java.util.List;

/**
 * A trait
 * Created by ediaz on 25-01-16.
 */
public class TraitDeclaration extends ContractDeclaration {

    private List<ContractMemberDeclaration> decls;
    private List<TypeParam> params;

    public TraitDeclaration(OguTypeIdentifier name, List<TypeParam> params, List<ContractMemberDeclaration> decls, List<Decorator> decorators) {
        super(name, decorators);
        this.decls = new ArrayList<>();
        this.decls.addAll(decls);
        this.decls.forEach((d) -> d.setParent(this));

        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }


}
