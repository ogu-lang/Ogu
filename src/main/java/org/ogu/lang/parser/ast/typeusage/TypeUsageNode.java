package org.ogu.lang.parser.ast.typeusage;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.codegen.jvm.JvmTypeCategory;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 11/4/16.
 */
public abstract class TypeUsageNode extends Node implements TypeUsage {

    public TypeUsage typeUsage() {
        return this;
    }

    public static TypeUsageNode wrap(TypeUsage typeUsage) {
        return new TypeUsageWrapperNode(typeUsage) {
            @Override
            public TypeUsageNode copy() {
                return this;
            }

            @Override
            public Iterable<Node> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public String toString() {
                return "TypeUsageWrapperNode{"+typeUsage+"}";
            }
        };
    }

    public static class TypeVariableData {
        private TypeVariableUsage.GenericDeclaration genericDeclaration;
        private List<? extends TypeUsage> bounds;

        public TypeVariableData(TypeVariableUsage.GenericDeclaration genericDeclaration, List<? extends TypeUsage> bounds) {
            this.genericDeclaration = genericDeclaration;
            this.bounds = bounds;
        }

        public TypeVariableUsage.GenericDeclaration getGenericDeclaration() {
            return genericDeclaration;
        }

        public List<? extends TypeUsage> getBounds() {
            return bounds;
        }
    }

    public static TypeUsage fromJvmType(JvmType jvmType, SymbolResolver resolver, Map<String, TypeVariableData> visibleGenericTypes) {
        Optional<PrimitiveTypeUsage> primitive = PrimitiveTypeUsage.findByJvmType(jvmType);
        if (primitive.isPresent()) {
            return primitive.get();
        }
        String signature = jvmType.getSignature();
        if (signature.startsWith("[")) {
            JvmType componentType = new JvmType(signature.substring(1));
            return new ArrayTypeUsage(fromJvmType(componentType, resolver, visibleGenericTypes));
        } else if (signature.startsWith("L") && signature.endsWith(";")) {
            String typeName = signature.substring(1, signature.length() - 1);
            typeName = typeName.replaceAll("/", ".");
            Optional<TypeDefinition> typeDefinition = resolver.findTypeDefinitionIn(typeName, null, resolver);
            if (!typeDefinition.isPresent()) {
                throw new RuntimeException("Unable to find definition of type " + typeName + " using " + resolver);
            }
            return new ReferenceTypeUsage(typeDefinition.get());
        } else if (signature.equals("V")) {
            return new VoidTypeUsage();
        } else {
            for (String typeVariableName : visibleGenericTypes.keySet()) {
                if (typeVariableName.equals(signature)) {
                    TypeVariableData typeVariableData = visibleGenericTypes.get(typeVariableName);
                    return new ConcreteTypeVariableUsage(typeVariableData.genericDeclaration, typeVariableName, typeVariableData.getBounds());
                }
            }
            throw new UnsupportedOperationException("Signature="+signature+", type="+jvmType.getClass());
        }
    }

    public final JvmTypeCategory toJvmTypeCategory() {
        return this.jvmType().typeCategory();
    }

    @Override
    public boolean isReferenceTypeUsage() {
        return false;
    }

    @Override
    public ReferenceTypeUsage asReferenceTypeUsage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayTypeUsage asArrayTypeUsage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public PrimitiveTypeUsage asPrimitiveTypeUsage() {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    public abstract TypeUsageNode copy();

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName());
    }
}
