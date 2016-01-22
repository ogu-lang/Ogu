package org.ogu.lang.parser.ast;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.uses.UsesDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class OguModule extends Node {

    ModuleNameDefinition nameDefinition;
    private List<Node> topNodes = new ArrayList<>();
    private List<UsesDeclaration> uses = new ArrayList<>();


    public void setName(ModuleNameDefinition nameDefinition) {
        if (this.nameDefinition != null) {
            this.nameDefinition.parent = null;
        }
        this.nameDefinition = nameDefinition;
        this.nameDefinition.parent = this;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(nameDefinition).addAll(topNodes).addAll(uses).build();
    }

    public ModuleNameDefinition getNameDefinition() {
        return nameDefinition;
    }
}
