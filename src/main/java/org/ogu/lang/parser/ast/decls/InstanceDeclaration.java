package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;

import java.util.List;

/**
 * instance T x where ...
 * Created by ediaz on 25-01-16.
 */
public class InstanceDeclaration extends ContractDeclaration {

    public InstanceDeclaration(OguTypeIdentifier name, List<TypeParam> params, List<ContractMemberDeclaration> decls, List<Decorator> decorators) {
        super(name, decorators);
    }
}
