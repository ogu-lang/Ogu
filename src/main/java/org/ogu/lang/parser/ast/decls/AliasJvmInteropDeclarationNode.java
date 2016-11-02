package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Created by ediaz on 11/1/16.
 */
public class AliasJvmInteropDeclarationNode extends AliasDeclarationNode {

    private String jvmSignature;


    public AliasJvmInteropDeclarationNode(NameNode id, List<DecoratorNode> decoratorNodes, String jvmSignature) {
        super(id, decoratorNodes);
        this.jvmSignature = jvmSignature;
    }

    @Override
    public TypeUsage calcType() {
        TypeDefinition typeDefinition = typeDefinition(symbolResolver());
        if (JvmNameUtils.isMethodSignature(jvmSignature)) {
            return typeDefinition.getFunctionFromJvmSignature(jvmSignature).getReturnType();
        } else {
            return typeDefinition.getFieldTypeFromJvmSignature(jvmSignature);
        }
    }

    private TypeDefinition typeDefinitionCache;


    private TypeDefinition typeDefinition(SymbolResolver resolver) {
        if (typeDefinitionCache == null) {
            typeDefinitionCache = resolver.getTypeDefinitionFromJvmSignature(jvmSignature, this);
        }
        return typeDefinitionCache;
    }


    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invocable) {
        if (!JvmNameUtils.isMethodSignature(jvmSignature)) {
            throw new UnsupportedOperationException(this.getClass().getCanonicalName());
        }
        TypeDefinition td = typeDefinition(symbolResolver());
        InternalFunctionDefinition def =  td.getFunctionFromJvmSignature(jvmSignature);
        Logger.debug("esto encontré: "+def);
        return Optional.of(def.getFormalParameters());
    }

    @Override
    public String toString() {
        return "AliasJvmInteropDeclaration{" +
                "jvmSignature=" + jvmSignature +
                ", decorators='" + decoratorNodes + '\''+
                '}';
    }
}
