package org.ogu.lang.parser.ast;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedTypeException;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.util.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class TypeIdentifierNode extends NameNode {

    public TypeIdentifierNode(String name) {
        super(name);
    }

    public TypeIdentifierNode(NameNode base, String name) {
        super(base, name);
    }


    @Override
    public String toString() {
        return "TypeIdentifier{" +
                "packageName=" + base +
                ", typeName='" + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeIdentifierNode that = (TypeIdentifierNode) o;

        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    public TypeDefinition resolve(SymbolResolver resolver) {
        Optional<TypeDefinition> res = resolver.findTypeDefinitionIn(qualifiedName(), this, resolver);
        if (res.isPresent()) {
            return res.get();
        } else {
            throw new UnsolvedTypeException(qualifiedName(), this);
        }
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }


    @Override
    public Iterable<Node> getChildren() {
        if (base == null) {
            return Collections.emptyList();
        } else {
            return ImmutableList.of(base);
        }
    }

    public static TypeIdentifierNode create(List<String> base) {

        if (base.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (base.size() == 1) {
            return new TypeIdentifierNode(base.get(0));
        } else {
            return new TypeIdentifierNode(TypeIdentifierNode.create(base.subList(0, base.size() - 1)), base.get(base.size() - 1));
        }
    }

    public static TypeIdentifierNode create(String path) {
        return create(Arrays.stream(path.split("\\.")).collect(Collectors.toList()));
    }

}
