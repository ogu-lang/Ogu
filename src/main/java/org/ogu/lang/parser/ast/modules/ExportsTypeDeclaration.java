package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.QualifiedName;

/**
 * Exports a Type
 * Created by ediaz on 22-01-16.
 */
public class ExportsTypeDeclaration extends ExportsDeclaration {

    private QualifiedName qualifiedName;

    @Override
    public String toString() {
        return "ExportsTypeDeclaration{" +
                "qualifiedName=" + qualifiedName +
                '}';
    }

    public ExportsTypeDeclaration(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.qualifiedName.setParent(this);
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(qualifiedName);
    }
}
