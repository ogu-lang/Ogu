package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * OguType is the base of all type declarations
 * Created by ediaz on 24-01-16.
 */
public abstract class TypeNode extends Node implements TypeUsage {

    protected TypeUsage typeUsage;

    public String getName()  {
        return null;
    }

    public TypeUsage typeUsage() {
        if (typeUsage == null) {
            throw new IllegalStateException();
        }
        return typeUsage;
    }
}
