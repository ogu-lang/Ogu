package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic args:
 *
 * Created by ediaz on 02-02-16.
 */
public class GenericTypeArgUsageNode extends TypeArgUsageWrapperNode {

    private TypeIdentifierNode name;
    private List<NameNode> args;

    public GenericTypeArgUsageNode(TypeIdentifierNode name, List<NameNode> args) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
    }

    @Override
    public TypeUsageNode copy() {
        return null;
    }

    @Override
    public String toString() {
        return "GenericType{"+
                "name="+name+
                ", args="+args+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).addAll(args).build();
    }


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }

}
