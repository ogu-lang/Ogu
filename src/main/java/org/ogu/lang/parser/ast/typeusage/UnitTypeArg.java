package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

/**
 * Unit type (def foo : -> ())
 * Created by ediaz on 23-01-16.
 */
public class UnitTypeArg extends TypeArg {

    public UnitTypeArg() {

    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Unit!";
    }
}
