package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguIdentifier;

import java.util.List;

/**
 * Exports a function
 * Created by ediaz on 22-01-16.
 */
public class ExportsFunctionDeclaration extends ExportsDeclaration {

    @Override
    public String toString() {
        return "ExportsFunctionDeclaration{" +
                "reference=" + name +
                ", decorators=" + decorators +
                '}';
    }

    public ExportsFunctionDeclaration(OguIdentifier referenceName, List<Decorator> decorators) {
        super(referenceName, decorators);
    }

}
