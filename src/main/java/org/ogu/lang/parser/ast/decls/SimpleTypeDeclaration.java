package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.List;

/**
 * A type like type T = ..
 * Created by ediaz on 24-01-16.
 */
public class SimpleTypeDeclaration extends TypeDeclaration {

    public SimpleTypeDeclaration(OguTypeIdentifier name, OguType type, List<Decorator> decorators) {
        super(name, type, decorators);
    }


    @Override
    public String toString() {
        return "SimpleTypeDeclaration{"+
                "name="+name+
                ", type="+type+
                ", decorators="+decorators+
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .add(type)
                .addAll(decorators).build();
    }
}
