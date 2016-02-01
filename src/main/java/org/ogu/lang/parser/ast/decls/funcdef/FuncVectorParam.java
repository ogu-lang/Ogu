package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncVectorParam extends FunctionPatternParam {

    private OguTypeIdentifier typeId;


    public FuncVectorParam(OguTypeIdentifier typeId) {
        super();
        this.typeId = typeId;
        this.typeId.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncArg_MatchType{"+
                "typeId="+typeId+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(typeId);
    }
}
