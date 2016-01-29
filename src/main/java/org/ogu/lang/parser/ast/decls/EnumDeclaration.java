package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.OguTypeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 28-01-16.
 */
public class EnumDeclaration extends AlgebraicDataTypeDeclaration {

    private List<OguIdentifier> values;

    public EnumDeclaration(OguTypeIdentifier name, List<OguIdentifier> values, List<OguTypeIdentifier> deriving, List<Decorator> decorators) {
        super(name, deriving, decorators);
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }
}
