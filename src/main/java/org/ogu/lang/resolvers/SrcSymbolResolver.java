package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasDeclaration;
import org.ogu.lang.parser.ast.decls.ExportableDeclaration;
import org.ogu.lang.parser.ast.decls.ValDeclaration;
import org.ogu.lang.parser.ast.modules.OguModule;
import org.ogu.lang.symbols.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Solve symbols inside ogu modules
 * Created by ediaz on 20-01-16.
 */
public class SrcSymbolResolver implements SymbolResolver {

    private Map<String, AliasDeclaration> aliasDefinitions;
    private Map<String, ExportableDeclaration> declarations;

    private SymbolResolver parent = null;

    public SrcSymbolResolver(List<OguModule> modules) {
        this.aliasDefinitions = new HashMap<>();
        this.declarations = new HashMap<>();

        for (OguModule module : modules) {
            for (AliasDeclaration aliasDeclaration : module.getAliases())  {
                aliasDefinitions.put(aliasDeclaration.getName(), aliasDeclaration);
            }
            for (ExportableDeclaration decl : module.getDeclarations()) {
                declarations.put(decl.getName(), decl);
            }
        }

    }

    @Override
    public SymbolResolver getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolResolver parent) {
        this.parent = parent;
    }

    @Override
    public Optional<Symbol> findSymbol(String name, Node context) {
        if (aliasDefinitions.containsKey(name)) {
            return Optional.of(aliasDefinitions.get(name));
        }
        if (declarations.containsKey(name)) {
            return Optional.of(declarations.get(name));
        }
        return Optional.empty();
    }
}
