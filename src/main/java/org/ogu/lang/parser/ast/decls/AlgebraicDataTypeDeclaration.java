package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for EnumDeclaration and DataDeclaration
 * Created by ediaz on 28-01-16.
 */
public abstract class AlgebraicDataTypeDeclaration extends TypeDeclaration {

    protected List<OguTypeIdentifier> deriving;

    protected AlgebraicDataTypeDeclaration(OguTypeIdentifier name, List<OguTypeIdentifier> deriving, List<Decorator> decorators) {
        super(name, decorators);
        this.deriving = new ArrayList<>();
        this.deriving.addAll(deriving);
        this.deriving.forEach((d) -> d.setParent(this));
    }
}
