package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageWrapperNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * A type like type T = ..
 * Created by ediaz on 24-01-16.
 */
public class SimpleTypeDeclarationNode extends TypedefDeclarationNode {

    public SimpleTypeDeclarationNode(TypeIdentifierNode name, TypeUsageWrapperNode type, List<DecoratorNode> decoratorNodes) {
        super(name, type, decoratorNodes);
    }

    @Override
    public JvmType jvmType() {
        return type.jvmType();
    }


    @Override
    public String toString() {
        return "SimpleTypeDeclaration{"+
                "name="+name+
                ", type="+type+
                ", decorators="+ decoratorNodes +
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .add(type)
                .addAll(decoratorNodes).build();
    }

    @Override
    public TypeUsage calcType() {
        return type.calcType();
    }


    @Override
    public TypeUsage getFieldType(String fieldName) {
        Logger.debug("getFieldType ("+fieldName+") = "+type);
        return type;
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunction(String functionName, List<ActualParamNode> actualParams) {
        return null;
    }

    public boolean isPrimitive() {
        return calcType().isPrimitive();
    }
}
