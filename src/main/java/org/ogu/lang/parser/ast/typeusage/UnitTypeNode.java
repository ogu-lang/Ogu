package org.ogu.lang.parser.ast.typeusage;

import java.util.Collections;
import org.ogu.lang.parser.ast.Node;

/**
 * ()
 * Created by ediaz on 30-01-16.
 */
public class UnitTypeNode extends TypeNode {

    public UnitTypeNode() {
        super();
    }

    @Override
    public String toString() {
        return "Unit!";
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
