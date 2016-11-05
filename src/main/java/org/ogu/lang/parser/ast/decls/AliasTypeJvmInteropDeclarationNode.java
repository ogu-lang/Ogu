package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.List;

/**
 * Created by ediaz on 11/3/16.
 */
public class AliasTypeJvmInteropDeclarationNode extends AliasDeclarationNode {

    private String jvmSignature;

    public AliasTypeJvmInteropDeclarationNode(TypeIdentifierNode target, String signature, List<DecoratorNode> decoratorNodes) {
        super(target, decoratorNodes);
        jvmSignature = signature.replaceAll("\"", "");
    }

    @Override
    public TypeUsage calcType() {
        TypeDefinition typeDefinition = typeDefinition(symbolResolver());
        if (JvmNameUtils.isClassSignature(jvmSignature)) {
            return typeDefinition.getClassFromJvmSignature(jvmSignature, symbolResolver()).calcType();
        }
        else if (JvmNameUtils.isMethodSignature(jvmSignature)) {
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

    @Override
    public String toString() {
        return "AliasTypeJvmInteropDeclarationNode{" +
                "name="+name+
                ", signature="+jvmSignature+
                "decorators='" + decoratorNodes + "\'}";
    }
}
