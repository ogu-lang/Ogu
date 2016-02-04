package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasDeclarationNode;
import org.ogu.lang.parser.ast.decls.ExportableDeclarationNode;
import org.ogu.lang.parser.ast.modules.ModuleNode;
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

    private Map<String, AliasDeclarationNode> aliasDefinitions;
    private Map<String, ExportableDeclarationNode> declarations;

    private SymbolResolver parent = null;

    public SrcSymbolResolver(List<ModuleNode> modules) {
        this.aliasDefinitions = new HashMap<>();
        this.declarations = new HashMap<>();

        for (ModuleNode module : modules) {
            for (AliasDeclarationNode aliasDeclaration : module.getAliases())  {
                aliasDefinitions.put(aliasDeclaration.getName(), aliasDeclaration);
            }
            for (ExportableDeclarationNode decl : module.getDeclarations()) {
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
