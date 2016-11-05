package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalConstructorDefinition;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 25-01-16.
 */
public abstract class TypeDeclarationNode extends ExportableDeclarationNode implements TypeDefinition {

    protected TypeDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }

    @Override
    public String getQualifiedName() {
        return null;
    }

    @Override
    public List<ReferenceTypeUsage> getAllAncestors() {
        return null;
    }

    @Override
    public TypeDefinition getSuperclass() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public TypeUsage getFieldType(String fieldName) {
        return null;
    }

    @Override
    public Symbol getFieldOnInstance(String fieldName, Symbol instance) {
        return null;
    }

    @Override
    public boolean hasField(String name, boolean staticContext) {
        return false;
    }

    @Override
    public boolean canFieldBeAssigned(String field) {
        return false;
    }

    @Override
    public JvmConstructorDefinition resolveConstructorCall(List<ActualParamNode> actualParams) {
        return null;
    }

    @Override
    public Optional<InternalConstructorDefinition> findConstructor(List<ActualParamNode> actualParams) {
        return null;
    }

    @Override
    public List<InternalConstructorDefinition> getConstructors() {
        return null;
    }

    @Override
    public JvmMethodDefinition findFunctionFor(String name, List<JvmType> argsTypes, boolean staticContext) {
        return null;
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunction(String functionName, List<ActualParamNode> actualParams) {
        return null;
    }

    @Override
    public <T extends TypeUsage> Map<String, TypeUsage> associatedTypeParametersToName(List<T> typeParams) {
        return null;
    }


    @Override
    public TypeUsage getFieldTypeFromJvmSignature(String jvmSignature) {
        return null;
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunctionFromJvmSignature(String jvmSignature) {
        throw new UnsupportedOperationException("por implementar findFunction: "+jvmSignature);
    }


}
