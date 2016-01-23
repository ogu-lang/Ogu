package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.QualifiedName;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.Symbol;

import java.util.Optional;

/**
 * uses QualifiedName
 * Created by ediaz on 21-01-16.
 */

public class UsesDeclaration extends Node {


    private QualifiedName qualifiedName;

    @Override
    public String toString() {
        return "UsesDeclaration{" +
                "qualifiedName=" + qualifiedName +
                '}';
    }

    public UsesDeclaration(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.qualifiedName.setParent(this);
    }

    public  Optional<Symbol> findAmongImported(String name, SymbolResolver resolver) {
        // TODO implement search
        return null;
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(qualifiedName);
    }
}