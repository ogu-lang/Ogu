package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Map;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncIdParamNode extends FunctionPatternParamNode {

    private IdentifierNode id;


    public FuncIdParamNode(IdentifierNode id) {
        super();
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

    @Override
    public TypeUsage getType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public FormalParameter apply(Map<String, TypeUsage> typeParams) {
        return null;
    }
}
