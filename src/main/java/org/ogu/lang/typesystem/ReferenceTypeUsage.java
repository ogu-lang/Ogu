package org.ogu.lang.typesystem;

import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.resolvers.jdk.ReflectionTypeDefinitionFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReferenceTypeUsage  implements TypeUsage {

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

}
