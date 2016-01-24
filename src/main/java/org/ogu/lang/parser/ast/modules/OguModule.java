package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.Declaration;
import org.ogu.lang.parser.ast.decls.ExportsDeclaration;
import org.ogu.lang.parser.ast.decls.UsesDeclaration;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.decls.AliasDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class OguModule extends Node {

    ModuleNameDefinition nameDefinition;
    private List<Expression> program = new ArrayList<>();
    private List<UsesDeclaration> uses = new ArrayList<>();
    private List<AliasDeclaration> aliases = new ArrayList<>();
    private List<ExportsDeclaration> exports = new ArrayList<>();
    private List<Declaration> decls = new ArrayList<>();


    public void add(Expression expression) {
        program.add(expression);
        expression.setParent(this);
    }

    public void add(Declaration decl) {
        decls.add(decl);
        decl.setParent(this);
    }

    public void add(AliasDeclaration alias) {
        aliases.add(alias);
        alias.setParent(this);
    }

    public void addExports(List<ExportsDeclaration> exportsDeclarations) {
        exports.addAll(exportsDeclarations);
        for (ExportsDeclaration exportsDeclaration : exportsDeclarations)
            exportsDeclaration.setParent(this);
    }

    public void addUses(List<UsesDeclaration> usesDeclarations) {
        uses.addAll(usesDeclarations);
        for (UsesDeclaration usesDeclaration : usesDeclarations)
            usesDeclaration.setParent(this);
    }

    public void setName(ModuleNameDefinition nameDefinition) {
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
                .addAll(decls)
                .addAll(aliases)
                .addAll(exports)
                .addAll(uses).build();
    }

    public ModuleNameDefinition getNameDefinition() {
        return nameDefinition;
    }
}
