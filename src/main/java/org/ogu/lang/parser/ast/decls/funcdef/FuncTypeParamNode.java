package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Map;

/**
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncTypeParamNode extends FunctionPatternParamNode {

    private TypeIdentifierNode typeId;


    public FuncTypeParamNode(TypeIdentifierNode typeId) {
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
