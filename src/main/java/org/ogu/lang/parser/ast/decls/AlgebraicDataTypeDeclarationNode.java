package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.TypeIdentifierNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for EnumDeclaration and DataDeclaration
 * Created by ediaz on 28-01-16.
 */
public abstract class AlgebraicDataTypeDeclarationNode extends TypeDeclarationNode {

    protected List<TypeIdentifierNode> deriving;

    protected AlgebraicDataTypeDeclarationNode(TypeIdentifierNode name, List<TypeIdentifierNode> deriving, List<Decorator> decorators) {
        super(name, decorators);
        this.deriving = new ArrayList<>();
        this.deriving.addAll(deriving);
        this.deriving.forEach((d) -> d.setParent(this));
    }
}
