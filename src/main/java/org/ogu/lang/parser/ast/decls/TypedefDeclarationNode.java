package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.List;

/**
 * All type declarations
 * Created by ediaz on 24-01-16.
 */
public abstract  class TypedefDeclarationNode extends TypeDeclarationNode {

    protected TypeNode type;

    protected TypedefDeclarationNode(TypeIdentifierNode name, TypeNode type, List<Decorator> decorators)  {
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
