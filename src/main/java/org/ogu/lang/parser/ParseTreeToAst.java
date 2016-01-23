package org.ogu.lang.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.ogu.lang.antlr.OguParser;
import org.ogu.lang.compiler.Ogu;
import org.ogu.lang.parser.ast.*;
import org.ogu.lang.parser.ast.decls.FunctionDeclaration;
import org.ogu.lang.parser.ast.decls.ValDeclaration;
import org.ogu.lang.parser.ast.expressions.ActualParam;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.expressions.FunctionCall;
import org.ogu.lang.parser.ast.expressions.Reference;
import org.ogu.lang.parser.ast.expressions.literals.StringLiteral;
import org.ogu.lang.parser.ast.modules.*;
import org.ogu.lang.parser.ast.typeusage.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.ogu.lang.util.Messages.error;

/**
 * Transforms from Antlr ParseTree to Ast.
 * Based on code from here: https://github.com/ftomassetti/turin-programming-language
 * Created by ediaz on 21-01-16.
 */
public class ParseTreeToAst {

    private org.ogu.lang.parser.ast.Position getPosition(ParserRuleContext ctx) {
        return new org.ogu.lang.parser.ast.Position(getStartPoint(ctx.start), getEndPoint(ctx.stop));
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

    public OguModule toAst(File file, org.ogu.lang.antlr.OguParser.ModuleContext ctx) {
        OguModule module = new OguModule();
        getPositionFrom(module, ctx);
        module.setName(toAst(file, ctx.moduleHeader));

        OguParser.Module_bodyContext bodyCtx = ctx.module_body();
        for (OguParser.Module_declContext memberCtx : bodyCtx.module_decl()) {
            Node memberNode = toAst(memberCtx);
            if (memberNode instanceof Expression)
                module.add((Expression) memberNode);
            else if (memberNode instanceof AliasDeclaration)
                module.add((AliasDeclaration) memberNode);
            else if (memberNode instanceof ValDeclaration)
                module.add((ValDeclaration) memberNode);
            else if (memberNode instanceof FunctionDeclaration)
                module.add((FunctionDeclaration) memberNode);
        }

        for (OguParser.Module_usesContext usesDeclarationContext : ctx.module_uses()) {
            module.addUses(toAst(usesDeclarationContext));
        }
        for (OguParser.Module_exportsContext exportsDeclarationContext : ctx.module_exports()) {
            module.addExports(toAst(exportsDeclarationContext));
        }
        return module;
    }

    private ModuleNameDefinition toAst(File file, OguParser.Module_headerContext ctx) {
        if (ctx == null)
            return new ModuleNameDefinition(buildModuleNameFromFileName(file.getName()));
        return new ModuleNameDefinition(toAst(ctx.name).qualifiedName());
    }

    private QualifiedName toAst(OguParser.Module_nameContext ctx) {
        QualifiedName qualifiedName = QualifiedName.create(ctx.parts.stream().map(Token::getText).collect(Collectors.toList()));
        getPositionFrom(qualifiedName, ctx);
        return qualifiedName;
    }

    private OguTypeIdentifier toOguTypeIdentifier(OguParser.Alias_targetContext ctx) {
        OguTypeIdentifier tname = OguTypeIdentifier.create(idText(ctx.alias_tid));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private OguTypeIdentifier toOguTypeIdentifier(OguParser.Alias_originContext ctx) {
        OguTypeIdentifier tname = OguTypeIdentifier.create(ctx.alias_origin_tid.stream().map(Token::getText).collect(Collectors.toList()));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private OguIdentifier toOguIdentifier(OguParser.Alias_targetContext ctx) {
        OguIdentifier tname = OguIdentifier.create(idText(ctx.alias_id));
        getPositionFrom(tname, ctx);
        return tname;
    }


    private OguIdentifier toOguIdentifier(OguParser.Alias_originContext ctx) {
        OguIdentifier tname = OguIdentifier.create(ctx.alias_origin_tid.stream().map(Token::getText).collect(Collectors.toList()), idText(ctx.alias_origin_id));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private ExportsDeclaration toAst(OguParser.Export_nameContext ctx) {
        ExportsDeclaration result;
        if (ctx.ID() != null)
            result = new ExportsFunctionDeclaration(new OguIdentifier(idText(ctx.ID().getSymbol())));
        else if (ctx.TID() != null)
            result = new ExportsTypeDeclaration(QualifiedName.create(idText(ctx.TID().getSymbol())));
        else {
            throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
        }
        getPositionFrom(result, ctx);
        return result;
    }

    private Node toAst(OguParser.Module_declContext ctx) {
        if (ctx.alias_def() != null) {
            return toAst(ctx.alias_def());
        }
        if (ctx.val_decl() != null) {
            return toAst(ctx.val_decl());
        }
        if (ctx.expr() != null) {
            return toAst(ctx.expr());
        }

        if (ctx.func_decl() != null) {
            return toAst(ctx.func_decl());
        }

        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionDeclaration toAst(OguParser.Func_declContext ctx) {

        if (ctx.name.f_id != null ) {
            List<TypeArg> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            return new FunctionDeclaration(toAst(ctx.name), params);
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TypeArg toAst(OguParser.Func_decl_argContext ctx) {
        if (ctx.unit() != null)
            return new UnitTypeArg();
        if (ctx.fda_id != null)
            return new QualifiedTypeArg(OguTypeIdentifier.create(idText(ctx.fda_id)));
        if (!ctx.fda_tid.isEmpty()) {
            if (ctx.fda_tid_tid_arg.isEmpty() && ctx.fda_tid_id_arg.isEmpty()) {
                OguTypeIdentifier id = OguTypeIdentifier.create(ctx.fda_tid.stream().map(this::idText).collect(Collectors.toList()));
                return new QualifiedTypeArg(id);
            }
        }
        throw new UnsupportedOperationException(ctx.toString());
    }

    private OguIdentifier toAst(OguParser.Func_name_declContext ctx) {
        if (ctx.f_id != null)
            return new OguIdentifier(idText(ctx.f_id));
        throw new UnsupportedOperationException(ctx.toString());
    }

    private List<ExportsDeclaration> toAst(OguParser.Module_exportsContext ctx) {
        if (ctx.export_name() != null)
            return ctx.exports.stream().map(this::toAst).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }

    private List<UsesDeclaration> toAst(OguParser.Module_usesContext ctx) {
        if (ctx.module_name() != null)
            return ctx.imports.stream().map((i) -> new UsesDeclaration(toAst(i))).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }

    private AliasDeclaration toAst(OguParser.Alias_defContext ctx) {
        OguParser.Alias_targetContext target = ctx.alias_target();
        OguParser.Alias_originContext origin = ctx.alias_origin();
        if (target.alias_tid != null) {
            if (origin.alias_origin_id != null) {
                return new AliasError(error("error.alias.tid_no_tid"), getPosition(ctx));
            }
            return new TypeAliasDeclaration(toOguTypeIdentifier(target), toOguTypeIdentifier(origin));
        } else {
          if (origin.alias_origin_id == null) {
              return new AliasError(error("error.alias.id_no_id"), getPosition(ctx));
          }
          return new IdAliasDeclaration(toOguIdentifier(target), toOguIdentifier(origin));
        }
    }

    private ValDeclaration toAst(OguParser.Val_declContext ctx) {
        if (ctx.val_id != null) {
            return new ValDeclaration(OguIdentifier.create(idText(ctx.val_id)), toAst(ctx.expr()));
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }
    private Expression toAst(OguParser.ExprContext ctx) {
        if (ctx.function != null) {
            return toAstFunctionCall(ctx);
        }
        if (ctx.qual_function != null) {
            return toAstFunctionCall(ctx);
        }
        else {
            throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
        }
    }

    private ActualParam toAstParam(OguParser.ExprContext ctx) {
        if (ctx.literal != null)
            return new ActualParam(toAst(ctx.atom()));
        if (ctx.function != null) {
            System.out.println("Ast Param ctx.function="+ctx.function);
            return new ActualParam(toAstFunctionCall(ctx));
        }
        else {
            throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
        }
    }

    private Expression toAst(OguParser.Func_nameContext ctx) {
        Reference id = new Reference(new OguIdentifier(idText(ctx.name)));
        getPositionFrom(id, ctx);
        return id;
    }

    private Expression toAst(OguParser.Qual_func_nameContext ctx) {
        OguIdentifier tname = OguIdentifier.create(ctx.qual.stream().map(Token::getText).collect(Collectors.toList()), idText(ctx.name));
        Reference id = new Reference(tname);
        getPositionFrom(id, ctx);
        return id;
    }


    private Expression toAst(OguParser.AtomContext atomCtx) {
        if (atomCtx.string_literal != null) {
            return new StringLiteral(idText(atomCtx.STRING().getSymbol()));
        }
        else {
            throw new UnsupportedOperationException(atomCtx.getClass().getCanonicalName());
        }
    }

    private FunctionCall toAstFunctionCall(OguParser.ExprContext ctx) {
        if (ctx.function != null) {
            Expression function = toAst(ctx.function);
            return new FunctionCall(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
        }
        if (ctx.qual_function != null) {
            Expression function = toAst(ctx.qual_function);
            return new FunctionCall(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }


    private String idText(Token token) {
        if (token.getText().startsWith("v#") || token.getText().startsWith("T#")) {
            return token.getText().substring(2);
        } else {
            return token.getText();
        }
    }

    private String buildModuleNameFromFileName(String name) {
        int pos = name.indexOf('.');
        return Character.toUpperCase(name.charAt(0))+name.substring(1, pos);
    }

}
