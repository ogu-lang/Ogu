package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class ModuleNode extends Node {

    ModuleNameNode nameDefinition;
    private List<UsesDeclarationNode> uses = new ArrayList<>();
    private List<AliasDeclarationNode> aliases = new ArrayList<>();
    private List<ExportsDeclarationNode> exports = new ArrayList<>();
    private List<ExportableDeclarationNode> declarations = new ArrayList<>();
    private List<ExpressionNode> program = new ArrayList<>();

    public List<AliasDeclarationNode> getAliases() { return aliases; }

    public List<ExpressionNode> getProgram() {
        return program;
    }

    public void add(ExpressionNode expressionNode) {
        program.add(expressionNode);
        expressionNode.setParent(this);
    }

    public void add(AliasDeclarationNode alias) {
        aliases.add(alias);
        alias.setParent(this);
    }

    public void add(ExportableDeclarationNode decl) {
        declarations.add(decl);
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
                .addAll(program)
                .addAll(declarations)
                .addAll(aliases)
                .addAll(exports)
                .addAll(uses).build();
    }

    public ModuleNameNode getNameDefinition() {
        return nameDefinition;
    }

    public List<ExportableDeclarationNode> getDeclarations() {
        return declarations;
    }
}
