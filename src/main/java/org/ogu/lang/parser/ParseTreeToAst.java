package org.ogu.lang.parser;

import org.ogu.lang.antlr.*;
import org.ogu.lang.parser.ast.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Transforms from Antlr ParseTree to Ast.
 * Based on code from here: https://github.com/ftomassetti/turin-programming-language
 * Created by ediaz on 21-01-16.
 */
public class ParseTreeToAst {

    private Position getPosition(ParserRuleContext ctx) {
        return new Position(getStartPoint(ctx.start), getEndPoint(ctx.stop));
    }

    private void getPositionFrom(Node node, ParserRuleContext ctx) {
        node.setPosition(getPosition(ctx));
    }

    private Point getStartPoint(Token token) {
        return new Point(token.getLine(), token.getCharPositionInLine());
    }

    private Point getEndPoint(Token token) {
        return new Point(token.getLine(), token.getCharPositionInLine() + token.getText().length());
    }

    public OguModule toAst(File file, OguParser.ModuleContext ctx) {
        OguModule module = new OguModule();
        getPositionFrom(module, ctx);
        module.setName(toAst(file, ctx.moduleHeader));
        return module;
    }

    public ModuleNameDefinition toAst(File file, OguParser.Module_headerContext ctx) {
        if (ctx == null)
            return new ModuleNameDefinition(file.getName());
        return new ModuleNameDefinition(toAst(ctx.name).qualifiedName());
    }

    private QualifiedName toAst(OguParser.Module_nameContext ctx) {
        QualifiedName qualifiedName = QualifiedName.create(ctx.parts.stream().map((p) -> p.getText()).collect(Collectors.toList()));
        getPositionFrom(qualifiedName, ctx);
        return qualifiedName;
    }


}
