package org.ogu.lang.resolvers;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedFunctionException;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasDeclarationNode;
import org.ogu.lang.parser.ast.decls.ExportableDeclarationNode;
import org.ogu.lang.parser.ast.decls.TypeDeclarationNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.parser.ast.modules.ModuleNameNode;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.util.Logger;

import java.util.*;

/**
 * Solve symbols inside ogu modules
 * Created by ediaz on 20-01-16.
 */
public class SrcSymbolResolver implements SymbolResolver {

    private Set<String> packages = new HashSet<>();
    private Map<String, AliasDeclarationNode> aliasDefinitions;
    private Map<String, ExportableDeclarationNode> declarations;
    private Map<String, TypeDefinition> typeDefinitions;
    private Map<String, ModuleNameNode> contextDefinitions;

    private SymbolResolver parent = null;

    public SrcSymbolResolver(List<ModuleNode> modules) {
        this.aliasDefinitions = new HashMap<>();
        this.declarations = new HashMap<>();
        this.typeDefinitions = new HashMap<>();

        for (ModuleNode module : modules) {
            for (AliasDeclarationNode aliasDeclaration : module.getAliases())  {
                aliasDefinitions.put(aliasDeclaration.getName(), aliasDeclaration);
            }
            for (ExportableDeclarationNode decl : module.getDeclarations()) {
                declarations.put(decl.getName(), decl);
            }
            for (TypeDeclarationNode typeDefinition : module.getTopLevelTypeDefinitions()) {
                packages.add(typeDefinition.contextName());
                typeDefinitions.put(typeDefinition.getQualifiedName(), typeDefinition);
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

    @Override
    public Optional<TypeDefinition> findTypeDefinitionIn(String typeName, Node context, SymbolResolver resolver) {
        if (typeDefinitions.containsKey(typeName)) {
            return Optional.of(typeDefinitions.get(typeName));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TypeDefinition> findTypeDefinitionFromJvmSignature(String jvmSignature, Node context, SymbolResolver resolver) {
        throw new UnsolvedSymbolException(context, jvmSignature);
    }

    @Override
    public Optional<JvmMethodDefinition> findJvmDefinition(FunctionCallNode functionCall) {
        throw new UnsolvedFunctionException(functionCall);
    }

    @Override
    public boolean existPackage(String packageName) {
        return packages.contains(packageName);
    }

}
