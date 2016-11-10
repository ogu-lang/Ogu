package org.ogu.lang.typesystem;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalConstructorDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.decls.SimpleTypeDeclarationNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.jdk.ReflectionTypeDefinitionFactory;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReferenceTypeUsage implements TypeUsage {

    public static final ReferenceTypeUsage OBJECT(SymbolResolver resolver) {
        return new ReferenceTypeUsage(
                ReflectionTypeDefinitionFactory.getInstance().getTypeDefinition(Object.class, resolver));
    }

    public static final ReferenceTypeUsage STRING(SymbolResolver resolver) {
        return new ReferenceTypeUsage(
                ReflectionTypeDefinitionFactory.getInstance().getTypeDefinition(String.class, resolver));
    }

    private List<TypeUsage> typeParams;
    private TypeParameterValues typeParameterValues = new TypeParameterValues();
    private TypeDefinition cachedTypeDefinition;

    public ReferenceTypeUsage(TypeDefinition typeDefinition, List<TypeUsage> typeParams) {
        this.typeParams = new ArrayList<>(typeParams);
        this.cachedTypeDefinition = typeDefinition;
    }

    public ReferenceTypeUsage(TypeDefinition td) {
        this(td, Collections.emptyList());
    }

    @Override
    public boolean isReference() { return true; }

    @Override
    public boolean isPrimitive() {
        TypeDefinition td = getTypeDefinition();
        if (td instanceof SimpleTypeDeclarationNode) {
            return ((SimpleTypeDeclarationNode) td).calcType().isPrimitive();
        }
        return false;
    }

    public boolean isInterface(SymbolResolver resolver) {
        return getTypeDefinition().isInterface();
    }

    public boolean isClass(SymbolResolver resolver) {
        return getTypeDefinition().isClass();
    }

    public boolean isEnum(SymbolResolver resolver) {
        throw new UnsupportedOperationException();
    }

    public TypeParameterValues getTypeParameterValues() {
        return typeParameterValues;
    }

    public TypeDefinition getTypeDefinition() {
        return cachedTypeDefinition;
    }

    @Override
    public JvmType jvmType() {
        return getTypeDefinition().jvmType();
    }

    public String getQualifiedName() {
        return getTypeDefinition().getQualifiedName();
    }

    @Override
    public boolean isReferenceTypeUsage() {
        return true;
    }

    @Override
    public ReferenceTypeUsage asReferenceTypeUsage() {
        return this;
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        if (!type.isReferenceTypeUsage()) {
            return false;
        }
        ReferenceTypeUsage other = type.asReferenceTypeUsage();
        if (this.getQualifiedName().equals(other.getQualifiedName())) {
            return true;
        }
        for (TypeUsage ancestor : this.getAllAncestors()) {
            if (ancestor.canBeAssignedTo(type)) {
                return true;
            }
        }
        return false;
    }

    public List<ReferenceTypeUsage> getAllAncestors() {
        // TODO perhaps some generic type substitution needs to be done
        return getTypeDefinition().getAllAncestors();
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        return getTypeDefinition().getFieldOnInstance(fieldName, instance);
    }

    @Override
    public boolean isInvocable() {
        return true;
    }

    @Override
    public Invocable asInvocable() {
        return new AsConstructor();
    }

    class AsConstructor implements Invocable {

        @Override
        public Optional<InternalConstructorDefinition> internalInvocableDefinitionFor(List<ActualParamNode> actualParams) {
            return getTypeDefinition().findConstructor(actualParams);
        }

        @Override
        public boolean isOverloaded() {
            return getTypeDefinition().hasManyConstructors();
        }

    }


    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        if (this.typeParams.size() == 0) {
            return this;
        }
        List<TypeUsage> replacedParams = this.typeParams.stream().map((tp)->tp.replaceTypeVariables(typeParams)).collect(Collectors.toList());
        if (!replacedParams.equals(this.typeParams)) {
            ReferenceTypeUsage copy = new ReferenceTypeUsage(this.cachedTypeDefinition);
            copy.typeParams = replacedParams;
            return copy;
        } else {
            return this;
        }
    }

    @Override
    public boolean sameType(TypeUsage other) {
        if (!other.isReferenceTypeUsage()) {
            return false;
        }
        return getQualifiedName().equals(other.asReferenceTypeUsage().getQualifiedName());
    }

    public Map<String, TypeUsage> typeParamsMap() {
        return getTypeDefinition().associatedTypeParametersToName(typeParams);
    }

    public class TypeParameterValues {
        private List<TypeUsage> usages = new ArrayList<>();
        private List<String> names = new ArrayList<>();

        public void add(String name, TypeUsage typeUsage) {
            names.add(name);
            usages.add(typeUsage);
        }

        public List<TypeUsage> getInOrder() {
            return usages;
        }

        public List<String> getNamesInOrder() {
            return names;
        }

        public TypeUsage getByName(String name) {
            for (int i=0; i<names.size(); i++) {
                if (names.get(i).equals(name)) {
                    return usages.get(i);
                }
            }
            throw new IllegalArgumentException(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TypeParameterValues)) return false;

            TypeParameterValues that = (TypeParameterValues) o;

            if (!names.equals(that.names)) return false;
            if (!usages.equals(that.usages)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = usages.hashCode();
            result = 31 * result + names.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "TypeParameterValues{" +
                    "usages=" + usages +
                    ", names=" + names +
                    '}';
        }
    }

    @Override
    public Optional<Invocable> getFunction(String method) {
        return getTypeDefinition().getFunction(method, typeParamsMap());
    }

    @Override
    public String describe() {
        return getQualifiedName();
    }

    @Override
    public String toString() { return "REF "+describe(); }
}
