package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalConstructorDefinition;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.InvocableDefinition;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.Invocable;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class ModuleNode extends Node implements TypeDefinition {

    private List<Node> topNodes = new ArrayList<>();
    ModuleNameNode nameDefinition;
    private List<UsesDeclarationNode> uses = new ArrayList<>();
    private List<AliasDeclarationNode> aliases = new ArrayList<>();
    private List<ExportsDeclarationNode> exports = new ArrayList<>();
    private List<FunctionalDeclarationNode> functions = new ArrayList<>();
    private List<TypeDeclarationNode> typeDecls = new ArrayList<>();
    private List<ExpressionNode> program = new ArrayList<>();

    public List<AliasDeclarationNode> getAliases() {
        return aliases;
    }

    public List<ExpressionNode> getProgram() {
        return program;
    }

    public void add(TypeDeclarationNode typeDefinition) {
        typeDecls.add(typeDefinition);
        topNodes.add(typeDefinition);
        typeDefinition.setParent(this);
    }

    public void add(ExpressionNode expressionNode) {
        program.add(expressionNode);
        expressionNode.setParent(this);
    }

    public void add(AliasDeclarationNode alias) {
        aliases.add(alias);
        alias.setParent(this);
    }

    private Map<String, List<InternalFunctionDefinition>> functionsByName;

    public void add(FunctionalDeclarationNode decl) {
        functions.add(decl);
        decl.setParent(this);
    }


    public void addExports(List<ExportsDeclarationNode> exportsDeclarations) {
        exports.addAll(exportsDeclarations);
        exports.forEach((e) -> e.setParent(this));
    }

    public void addUses(List<UsesDeclarationNode> usesDeclarations) {
        uses.addAll(usesDeclarations);
        for (UsesDeclarationNode usesDeclaration : usesDeclarations)
            usesDeclaration.setParent(this);
    }

    public void setName(ModuleNameNode nameDefinition) {
        if (this.nameDefinition != null) {
            this.nameDefinition.setParent(this);
        }
        this.nameDefinition = nameDefinition;
        this.nameDefinition.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(nameDefinition)
                .addAll(uses)
                .addAll(aliases)
                .addAll(functions)
                .addAll(exports)
                .addAll(program)
                .build();
    }

    public ModuleNameNode getNameDefinition() {
        return nameDefinition;
    }

    public List<FunctionalDeclarationNode> getFunctions() {
        return functions;
    }

    public List<TypeDeclarationNode> getTopLevelTypeDefinitions() {
        return topNodes.stream().filter((n) -> (n instanceof TypeDeclarationNode)).map((n) -> (TypeDeclarationNode) n).collect(Collectors.toList());
    }

    @Override
    public Optional<Symbol> findSymbol(String name, SymbolResolver resolver) {
        for (UsesDeclarationNode usesDeclarationNode : uses) {
            Optional<Symbol> used = usesDeclarationNode.findAmongImported(name, resolver);
            if (used.isPresent()) {
                return Optional.of(used.get());
            }
        }
        for (AliasDeclarationNode aliasDeclarationNode : aliases) {
            if (aliasDeclarationNode.getName().equals(name)) {
                return Optional.of(aliasDeclarationNode);
            }
        }
        for (ExportsDeclarationNode export : exports) {
            if (export.getName().equals(name)) {
                return Optional.of(export);
            }
        }
        for (ExportableDeclarationNode decl : functions) {
            if (decl.getName().equals(name)) {
                return Optional.of(decl);
            }
        }
        for (ExportableDeclarationNode decl : typeDecls) {
            if (decl.getName().equals(name)) {
                return Optional.of(decl);
            }
        }

        String qName = nameDefinition.getName() + "." + name;
        return resolver.getRoot().findSymbol(qName, null);
    }


    @Override
    public Optional<Invocable> getFunction(String function, Map<String, TypeUsage> stringTypeUsageMap) {
        Logger.debug("get FUnction???"+function);
        throw new RuntimeException("WTF!!");
    }

        @Override
    public String getName() {
        return null;
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
    public TypeUsage getFieldTypeFromJvmSignature(String jvmSignature) {
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
    public JvmMethodDefinition findFunctionFor(String name, List<JvmType> argsTypes) {
        return null;
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunctionFromJvmSignature(String jvmSignature) {
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

}