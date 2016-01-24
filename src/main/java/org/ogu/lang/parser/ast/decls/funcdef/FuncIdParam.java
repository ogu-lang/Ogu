package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncIdParam extends FunctionPatternParam {

    private OguIdentifier id;


    public FuncIdParam(OguIdentifier id) {
        this.id = id;
        this.id.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncArg_MatchId{"+
                "id="+id+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(id);
    }
}
