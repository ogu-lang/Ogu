package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ValueReference;

/**
 * Exports a function
 * Created by ediaz on 22-01-16.
 */
public class ExportsFunctionDeclaration extends ExportsDeclaration {

    private ValueReference referenceName;

    @Override
    public String toString() {
        return "ExportsFunctionDeclaration{" +
                "referenceName=" + referenceName +
                '}';
    }

    public ExportsFunctionDeclaration(ValueReference referenceName) {
        this.referenceName = referenceName;
        this.referenceName.setParent(this);
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(referenceName);
    }
}
