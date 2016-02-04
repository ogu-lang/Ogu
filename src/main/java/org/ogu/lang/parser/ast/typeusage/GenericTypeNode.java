package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;

import java.util.ArrayList;
import java.util.List;

/**
 * type StrMap v = {String -> v}
 * val m : MapStr Int
 * Created by ediaz on 02-02-16.
 */
public class GenericTypeNode extends TypeNode {

    private TypeIdentifierNode name;
    private List<TypeNode> args;

    public GenericTypeNode(TypeIdentifierNode name, List<TypeNode> args) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
    }

    public String getName() {
        return name.getName();
    }

    public List<TypeNode> getArgs() {
        return args;
    }

    public TypeNode getArg(int i) {
        return args.get(i);
    }

    public void setArg(int i, TypeNode newType) {
        args.set(i, newType);
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

}
