package org.ogu.lang.definitions;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedConstructorException;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedFunctionException;
import org.ogu.lang.parser.ast.Named;
import org.ogu.lang.parser.ast.QualifiedName;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.jdk.ReflectionBasedTypeDefinition;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.Invocable;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 21-01-16.
 */
public interface TypeDefinition extends Symbol, Named {

    String getQualifiedName();

    List<ReferenceTypeUsage> getAllAncestors();

    TypeDefinition getSuperclass();

    boolean isInterface();

    boolean isClass();

    default JvmType jvmType() {
        return new JvmType(JvmNameUtils.canonicalToDescriptor(getQualifiedName()));
    }


    TypeUsage getFieldType(String fieldName);
    TypeUsage getFieldTypeFromJvmSignature(String jvmSignature);

    Symbol getFieldOnInstance(String fieldName, Symbol instance);

    boolean hasField(String name, boolean staticContext);

    default boolean hasField(QualifiedName fieldName, boolean staticContext) {
        if (!fieldName.isSimpleName()) {
            String firstName = fieldName.firstSegment();
            if (!hasField(firstName, staticContext)) {
                return false;
            }
            Symbol field = getField(firstName);
            TypeUsage typeUsage = field.calcType();
            if (typeUsage.isReferenceTypeUsage()) {
                TypeDefinition typeOfFirstField = typeUsage.asReferenceTypeUsage().getTypeDefinition();
                return typeOfFirstField.hasField(fieldName.rest(), true) || typeOfFirstField.hasField(fieldName.rest(), false);
            } else {
                return false;
            }
        }
        return hasField(fieldName.getName(), staticContext);
    }

    boolean canFieldBeAssigned(String field);

    JvmConstructorDefinition resolveConstructorCall(List<ActualParamNode> actualParams);

    default boolean hasManyConstructors() {
        return getConstructors().size() > 1;
    }

    default List<? extends FormalParameter> getConstructorParams(List<ActualParamNode> actualParams) {
        return getConstructor(actualParams).getFormalParameters();
    }

    default Optional<JvmConstructorDefinition> findConstructorDefinition(List<ActualParamNode> actualParams) {
        Optional<InternalConstructorDefinition> res = findConstructor(actualParams);
        if (res.isPresent()) {
            return Optional.of(res.get().getJvmConstructorDefinition());
        } else {
            return Optional.empty();
        }
    }

    default InternalConstructorDefinition getConstructor(List<ActualParamNode> actualParams) {
        Optional<InternalConstructorDefinition> constructor = findConstructor(actualParams);
        if (constructor.isPresent()) {
            return constructor.get();
        } else {
            throw new UnsolvedConstructorException(getQualifiedName(), actualParams);
        }
    }

    Optional<InternalConstructorDefinition> findConstructor(List<ActualParamNode> actualParams);

    List<InternalConstructorDefinition> getConstructors();


    default TypeUsage returnTypeWhenInvokedWith(String functionName, List<ActualParamNode> actualParams) {
        return getFunction(functionName, actualParams).getReturnType();
    }

    default List<? extends FormalParameter> getMethodParams(String functionName, List<ActualParamNode> actualParams, boolean staticContext) {
        return getFunction(functionName, actualParams).getFormalParameters();
    }

    default boolean hasFunctionFor(String functionName, List<ActualParamNode> actualParams) {
        return findFunction(functionName, actualParams).isPresent();
    }

    default InternalFunctionDefinition getFunction(String functionName, List<ActualParamNode> actualParams) {
        Optional<InternalFunctionDefinition> function = findFunction(functionName, actualParams);
        if (function.isPresent()) {
            return function.get();
        } else {
            throw new UnsolvedFunctionException(getQualifiedName(), functionName, actualParams);
        }
    }

    default InternalFunctionDefinition getFunctionFromJvmSignature(String jvmSignature) {
        Optional<InternalFunctionDefinition> function = findFunctionFromJvmSignature(jvmSignature);
        if (function.isPresent()) {
            return function.get();
        } else {
            throw new UnsupportedOperationException("No pudo encontrar funcion con esta firma: "+jvmSignature);
        }
    }

    default TypeDefinition getClassFromJvmSignature(String jvmSignature, SymbolResolver resolver) {
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(jvmSignature);
            return new ReflectionBasedTypeDefinition(clazz, resolver);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }


    JvmMethodDefinition findFunctionFor(String name, List<JvmType> argsTypes, boolean staticContext);

    Optional<InternalFunctionDefinition> findFunctionFromJvmSignature(String jvmSignature);

    Optional<InternalFunctionDefinition> findFunction(String functionName, List<ActualParamNode> actualParams);

    default Optional<Invocable> getFunction(String function, Map<String, TypeUsage> stringTypeUsageMap) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    <T extends TypeUsage> Map<String, TypeUsage> associatedTypeParametersToName(List<T> typeParams);
}
