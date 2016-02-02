package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.typeusage.OguType;

import java.util.List;

/**
 * All type declarations
 * Created by ediaz on 24-01-16.
 */
public abstract  class TypedefDeclaration extends TypeDeclaration {

    protected OguType type;

    protected TypedefDeclaration(OguTypeIdentifier name, OguType type, List<Decorator> decorators)  {
        super(name, decorators);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeDeclaration{"+
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
