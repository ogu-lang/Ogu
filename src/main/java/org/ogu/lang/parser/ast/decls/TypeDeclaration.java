package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.types.OguType;

import java.util.List;

/**
 * All type declarations
 * Created by ediaz on 24-01-16.
 */
public class TypeDeclaration extends ExportableDeclaration {

    private OguType type;

    public TypeDeclaration(OguTypeIdentifier name, OguType type, List<Decorator> decorators)  {
        super(name, decorators);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeDeclaration{"+
                "name="+name+
                ", type="+type+
                '}';
    }
}
