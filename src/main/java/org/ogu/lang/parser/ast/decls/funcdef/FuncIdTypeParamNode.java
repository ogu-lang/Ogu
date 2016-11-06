package org.ogu.lang.parser.ast.decls.funcdef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.typeusage.QualifiedTypeArgUsageNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageWrapperNode;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.FormalParameterSymbol;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.Map;

/**
 * id : T
 * and id for pattern matching
 * Created by ediaz on 23-01-16.
 */
public class FuncIdTypeParamNode extends FunctionPatternParamNode {

    private IdentifierNode id;
    private TypeUsageWrapperNode type;


    public FuncIdTypeParamNode(IdentifierNode id, TypeUsageWrapperNode type) {
        super();
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        return "FuncIdTypeParamNode{"+
                "id="+id+
                ", type="+type+
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuncIdTypeParamNode)) return false;

        FuncIdTypeParamNode that = (FuncIdTypeParamNode) o;

        if (!id.equals(that.id)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public TypeUsage calcType() {
        return type.calcType();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(id).add(type).build();
    }

    @Override
    public TypeUsage getType() {
        return type;
    }

    @Override
    public String getName() {
        return id.getName();
    }

    @Override
    public FormalParameter apply(Map<String, TypeUsage> typeParams) {
        return new FormalParameterSymbol(type.replaceTypeVariables(typeParams), id.getName());
    }
}
