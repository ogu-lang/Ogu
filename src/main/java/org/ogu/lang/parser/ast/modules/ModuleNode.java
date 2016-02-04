package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class ModuleNode extends Node {

    ModuleNameNode nameDefinition;
    private List<UsesDeclaration> uses = new ArrayList<>();
    private List<AliasDeclaration> aliases = new ArrayList<>();
    private List<ExportsDeclaration> exports = new ArrayList<>();
    private List<ExportableDeclaration> declarations = new ArrayList<>();
    private List<Expression> program = new ArrayList<>();

    public List<AliasDeclaration> getAliases() { return aliases; }

    public List<Expression> getProgram() {
        return program;
    }

    public void add(Expression expression) {
        program.add(expression);
        expression.setParent(this);
    }

    public void add(AliasDeclaration alias) {
        aliases.add(alias);
        alias.setParent(this);
    }

    public void add(ExportableDeclaration decl) {
        declarations.add(decl);
        decl.setParent(this);
    }

    public void addExports(List<ExportsDeclaration> exportsDeclarations) {
        exports.addAll(exportsDeclarations);
        exports.forEach((e) -> e.setParent(this));
    }

    public void addUses(List<UsesDeclaration> usesDeclarations) {
        uses.addAll(usesDeclarations);
        for (UsesDeclaration usesDeclaration : usesDeclarations)
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

    public List<ExportableDeclaration> getDeclarations() {
        return declarations;
    }
}
