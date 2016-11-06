package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.codegen.jvm.JvmFieldDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.jdk.ReflectionBasedField;
import org.ogu.lang.resolvers.jdk.ReflectionBasedMethodResolution;
import org.ogu.lang.resolvers.jdk.ReflectionBasedSetOfOverloadedMethods;
import org.ogu.lang.resolvers.jdk.ReflectionTypeDefinitionFactory;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by ediaz on 11/1/16.
 */
public class AliasJvmInteropDeclarationNode extends AliasDeclarationNode {

    private String jvmSignature;


    public AliasJvmInteropDeclarationNode(NameNode id, List<DecoratorNode> decoratorNodes, String jvmSignature) {
        super(id, decoratorNodes);
        this.jvmSignature = jvmSignature.replaceAll("\"", "");
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
        return Optional.of(def.getFormalParameters());
    }

    public JvmFieldDefinition getStaticField() {
        Optional<Field> fieldOpt = ReflectionBasedMethodResolution.findFieldByJvmSignature(jvmSignature);
        if (!fieldOpt.isPresent()) {
            throw new RuntimeException("No existe campo con esta firma:"+jvmSignature);
        }
        Field field = fieldOpt.get();
        TypeUsage fieldType = ReflectionTypeDefinitionFactory.toTypeUsage(field.getType(), symbolResolver());
        TypeUsage ownerType = ReflectionTypeDefinitionFactory.toTypeUsage(field.getDeclaringClass(), symbolResolver());
        return new JvmFieldDefinition(ownerType.jvmType().getInternalName(), field.getName(), fieldType.jvmType().getSignature(), true);
    }

    public JvmMethodDefinition findFunctionFor(List<ActualParamNode> argsTypes, SymbolResolver resolver) {
        Optional<Method> m = ReflectionBasedMethodResolution.findMethodByJvmSignature(jvmSignature);
        if (!m.isPresent()) {
            throw new RuntimeException("No existe un metodo con esta firma: "+jvmSignature);
        }

        return ReflectionTypeDefinitionFactory.toFunctionDefinition(m.get());
    }



    @Override
    public String toString() {
        return "AliasJvmInteropDeclaration{" +
                "jvmSignature=" + jvmSignature +
                ", decorators='" + decoratorNodes + '\''+
                '}';
    }
}
