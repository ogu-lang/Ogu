package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.OguTypeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic args:
 *
 * Created by ediaz on 02-02-16.
 */
public class GenericTypeArg extends TypeArg {

    private OguTypeIdentifier name;
    private List<OguName> args;

    public GenericTypeArg(OguTypeIdentifier name, List<OguName> args) {
        super();
        this.name = name;
        this.name.setParent(this);
        this.args = new ArrayList<>();
        this.args.addAll(args);
        this.args.forEach((a) -> a.setParent(this));
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
