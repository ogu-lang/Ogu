package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncIdTypeParam extends FunctionPatternParam {

    private IdentifierNode id;
    private TypeNode type;


    public FuncIdTypeParam(IdentifierNode id, TypeNode type) {
        super();
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncArg_MatchIdType{"+
                "id="+id+
                ", type="+type+
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(id).add(type).build();
    }

}
