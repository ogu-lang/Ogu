package org.ogu.lang.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.ogu.lang.antlr.OguParser;
import org.ogu.lang.parser.ast.*;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.decls.funcdef.FuncIdParam;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamConstrained;
import org.ogu.lang.parser.ast.expressions.*;
import org.ogu.lang.parser.ast.expressions.literals.StringLiteral;
import org.ogu.lang.parser.ast.modules.*;
import org.ogu.lang.parser.ast.typeusage.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.ogu.lang.util.Messages.message;

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
            else if (memberNode instanceof ValMemberDeclaration)
                module.add((ExportableDeclaration) memberNode);
            else if (memberNode instanceof FunctionMemberDeclaration)
                module.add((ExportableDeclaration) memberNode);
            else if (memberNode instanceof LetDefinition)
                module.add((ExportableDeclaration) memberNode);
        }

        for (OguParser.Module_usesContext usesDeclarationContext : ctx.module_uses()) {
            module.addUses(toAst(usesDeclarationContext, toAstDecorators(usesDeclarationContext.decs)));
        }
        for (OguParser.Module_exportsContext exportsDeclarationContext : ctx.module_exports()) {
            module.addExports(toAst(exportsDeclarationContext, toAstDecorators(exportsDeclarationContext.decs)));
        }
        return module;
    }

    private ModuleNameDefinition toAst(File file, OguParser.Module_headerContext ctx) {
        if (ctx == null)
            return new ModuleNameDefinition(buildModuleNameFromFileName(file.getName()));
        return new ModuleNameDefinition(toAst(ctx.name).qualifiedName());
    }

    private OguTypeIdentifier toAst(OguParser.Module_nameContext ctx) {
        OguTypeIdentifier type = OguTypeIdentifier.create(ctx.parts.stream().map(Token::getText).collect(Collectors.toList()));
        getPositionFrom(type, ctx);
        return type;
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

    private ExportsDeclaration toAst(OguParser.Export_nameContext ctx, List<Decorator> decs) {
        ExportsDeclaration result;
        if (ctx.ID() != null)
            result = new ExportsFunctionDeclaration(new OguIdentifier(idText(ctx.ID().getSymbol())), decs);
        else if (ctx.TID() != null)
            result = new ExportsTypeDeclaration(OguTypeIdentifier.create(idText(ctx.TID().getSymbol())), decs);
        else {
            throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
        }
        getPositionFrom(result, ctx);
        return result;
    }

    private Node toAst(OguParser.Module_declContext ctx) {
        List<Decorator> decs = toAstDecorators(ctx.decs);

        if (ctx.alias_def() != null) {
            return toAst(ctx.alias_def(), decs);
        }
        if (ctx.val_def() != null) {
            return toAst(ctx.val_def(), decs);
        }
        if (ctx.func_decl() != null) {
            return toAst(ctx.func_decl(), decs);
        }
        if (ctx.func_def() != null) {
            return toAst(ctx.func_def(), decs);
        }

        if (ctx.type_def() != null) {
            return toAst(ctx.type_def(), decs);
        }

        if (ctx.trait_def() != null) {
            return toAst(ctx.trait_def(), decs);
        }

        if (ctx.data_def() != null) {
            return toAst(ctx.data_def(), decs);
        }

        if (ctx.enum_def() != null) {
            return toAst(ctx.enum_def(), decs);
        }

        if (ctx.class_def() != null) {
            return toAst(ctx.class_def(), decs);
        }

        if (ctx.expr() != null) {
            return toAst(ctx.expr());
        }

        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private Node toAst(OguParser.Class_defContext ctx, List<Decorator> decs) {
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }


    private Node toAst(OguParser.Enum_defContext ctx, List<Decorator> decs) {
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private Node toAst(OguParser.Data_defContext ctx, List<Decorator> decs) {
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TraitDeclaration toAst(OguParser.Trait_defContext ctx, List<Decorator> decs) {
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TypeDeclaration toAst(OguParser.Type_defContext ctx, List<Decorator> decs) {
        Map<String,OguType> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);


        OguTypeIdentifier name = new OguTypeIdentifier(idText(ctx.t));
        OguType type = toAst(ctx.type());
        getPositionFrom(name, ctx);
        if (ctx.ta == null)  {
            SimpleTypeDeclaration tdecl = new SimpleTypeDeclaration(name, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        } else {
            // TODO Warning if are unused contraints
            List<TypeParam> params = new ArrayList<>();
            for (Token param : ctx.typedef_params().params) {
                String id = idText(param);
                TypeParam p;
                if (constraints.containsKey(id))
                    p = new TypeParamConstrained(id, constraints.get(id));
                else
                    p = new TypeParam(id);
                getPositionFrom(p, ctx);
                params.add(p);
            }
            GenericTypeDeclaration tdecl = new GenericTypeDeclaration(name, params, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        }
    }

    private void loadTypeConstraints(OguParser.Typedef_args_constraintsContext ctx, Map<String, OguType> cons) {
        for (OguParser.Typedef_arg_constraintContext tac:ctx.tac) {
            OguType type = toAst(tac.type());
            for (Token id : tac.ids) {
                cons.put(idText(id), type);
            }
        }
    }

    private LetDefinition toAst(OguParser.Func_defContext ctx, List<Decorator> decorators) {
        if (ctx.let_func_name != null) {
            if (ctx.let_func_name.lid_fun_id != null) {
                OguIdentifier funcId = OguIdentifier.create(idText(ctx.let_func_name.lid_fun_id));
                List<FunctionPatternParam> params = funcArgsToAst(ctx.let_func_args);
                LetDefinition funcdef = new LetDefinition(funcId, params, decorators);
                getPositionFrom(funcdef, ctx);
                if (ctx.expr() != null) {
                    Expression expr = toAst(ctx.expr());
                    funcdef.add(expr);
                } else if (ctx.let_expr() != null) {
                    toAst(ctx.let_expr(), funcdef);

                }
                return funcdef;
            }
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void toAst(OguParser.Let_exprContext ctx, LetDefinition funcdef) {
        if (ctx.let_block() != null) {
            toAst(ctx.let_block(), funcdef);
            return;
        }

        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void toAst(OguParser.Let_blockContext ctx, LetDefinition funcdef) {
        for (OguParser.Let_declContext decl : ctx.ld) {
            if (decl.expr() != null)
                funcdef.add(toAst(decl.expr()));
            else
                throw new UnsupportedOperationException(decl.getClass().getCanonicalName());
        }
    }


    private List<FunctionPatternParam> funcArgsToAst(List<OguParser.Let_argContext> let_func_args) {
        return let_func_args.stream().map(this::toAst).collect(Collectors.toList());
    }

    private FunctionPatternParam toAst(OguParser.Let_argContext ctx) {
        if (ctx.let_arg_atom() != null)
            return toAst(ctx.let_arg_atom());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionPatternParam toAst(List<OguParser.Let_arg_atomContext> params) {
        if (params.size() == 1) {
            OguParser.Let_arg_atomContext atom = params.get(0);
            if (atom.l_id != null && atom.l_id.lid_fun_id != null) {
                FuncIdParam id = toAst(atom.l_id.lid_fun_id);
                getPositionFrom(id, atom);
                return id;
            }
        }
        throw new UnsupportedOperationException(params.getClass().getCanonicalName());
    }

    private FuncIdParam toAst(Token tok) {
        return new FuncIdParam(OguIdentifier.create(idText(tok)));
    }

    private List<Decorator> toAstDecorators(OguParser.DecoratorsContext decs) {
        if (decs == null)
            return Collections.emptyList();
        return decs.dec.stream().map(this::toAst).collect(Collectors.toList());
    }

    private Decorator toAst(OguParser.DecoratorContext ctx) {
        String decoratorId = idText(ctx.dec_id);
        if ("extern".equals(decoratorId)) {
            List<String> decoratorArgs = ctx.dec_args.stream().map(this::idText).collect(Collectors.toList());
            if (decoratorArgs.size() != 2)
                return new DecoratorError(message("error.decorator.wrong_size_of_arguments"), getPosition(ctx));
            Decorator decorator = new ExternDecorator(decoratorArgs.get(0), decoratorArgs.get(1));
            getPositionFrom(decorator, ctx);
            return decorator;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionMemberDeclaration toAst(OguParser.Func_declContext ctx, List<Decorator> decorators) {
        if (ctx.name.f_id != null) {
            List<TypeArg> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            FunctionMemberDeclaration funcDecl = new FunctionMemberDeclaration(toAst(ctx.name), params, decorators);
            getPositionFrom(funcDecl, ctx);
            return funcDecl;
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
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private OguIdentifier toAst(OguParser.Func_name_declContext ctx) {
        if (ctx.f_id != null)
            return new OguIdentifier(idText(ctx.f_id));
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private List<ExportsDeclaration> toAst(OguParser.Module_exportsContext ctx, List<Decorator> decorators) {
        if (ctx.export_name() != null)
            return ctx.exports.stream().map((e) -> toAst(e, decorators)).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }


    private List<UsesDeclaration> toAst(OguParser.Module_usesContext ctx, List<Decorator> decs) {
        if (ctx.module_name() != null)
            return ctx.imports.stream().map((i) -> new UsesDeclaration(toAst(i), decs)).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }

    private AliasDeclaration toAst(OguParser.Alias_defContext ctx, List<Decorator> decs) {
        OguParser.Alias_targetContext target = ctx.alias_target();
        OguParser.Alias_originContext origin = ctx.alias_origin();
        if (target.alias_tid != null) {
            if (origin.alias_origin_id != null) {
                return new AliasError(message("error.alias.tid_no_tid"), getPosition(ctx));
            }
            TypeAliasDeclaration decl = new TypeAliasDeclaration(toOguTypeIdentifier(target), toOguTypeIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        } else {
            if (origin.alias_origin_id == null) {
                return new AliasError(message("error.alias.id_no_id"), getPosition(ctx));
            }
            IdAliasDeclaration decl = new IdAliasDeclaration(toOguIdentifier(target), toOguIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        }
    }

    private ValMemberDeclaration toAst(OguParser.Val_defContext ctx, List<Decorator> decorators) {
        if (ctx.val_id != null) {
            if (ctx.type() == null) {
                ValMemberDeclaration val = new ValMemberDeclaration(OguIdentifier.create(idText(ctx.val_id)), toAst(ctx.expr()), decorators);
                getPositionFrom(val, ctx);
                return val;
            }
            ValMemberDeclaration val = new ValMemberDeclaration(OguIdentifier.create(idText(ctx.val_id)), toAst(ctx.type()), toAst(ctx.expr()), decorators);
            getPositionFrom(val, ctx);
            return val;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private OguType toAst(OguParser.TypeContext ctx) {
        if (ctx.tid() != null) {
            if (ctx.t_a.isEmpty()) {
                return new QualifiedTypeArg(toAst(ctx.tid()));
            }
        } else if (ctx.nat != null) {
            OguNativeType type = new OguNativeType(idText(ctx.nat));
            getPositionFrom(type, ctx);
            return type;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private OguTypeIdentifier toAst(OguParser.TidContext ctx) {
        return OguTypeIdentifier.create(ctx.t.stream().map(this::idText).collect(Collectors.toList()));
    }


    private Expression toAst(OguParser.ExprContext ctx) {
        if (ctx.constructor() != null) {
            return toAst(ctx.constructor());
        }
        if (ctx.function != null) {
            return toAstFunctionCall(ctx);
        }
        if (ctx.qual_function != null) {
            return toAstFunctionCall(ctx);
        }
        if (ctx.ref != null) {
            Reference ref = new Reference(OguIdentifier.create(idText(ctx.ref)));
            getPositionFrom(ref, ctx);
            return ref;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private Constructor toAst(OguParser.ConstructorContext ctx) {
        TypeReference type = new TypeReference(toAst(ctx.tid()));
        getPositionFrom(type, ctx);
        List<ActualParam> params = ctx.expr_list().expr().stream().map(this::toAstParam).collect(Collectors.toList());
        Constructor ctor = new Constructor(type, params);
        getPositionFrom(ctor, ctx);
        return ctor;
    }

    private ActualParam toAstParam(OguParser.ExprContext ctx) {
        if (ctx.ref != null) {
            OguIdentifier id = OguIdentifier.create(idText(ctx.ref));
            getPositionFrom(id, ctx);
            Reference ref = new Reference(id);
            getPositionFrom(ref, ctx);
            ActualParam param = new ActualParam(ref);
            getPositionFrom(param, ctx);
            return param;
        }
        if (ctx.literal != null) {
            ActualParam param = new ActualParam(toAst(ctx.atom()));
            getPositionFrom(param, ctx);
            return param;
        }
        if (ctx.function != null) {
            ActualParam param = new ActualParam(toAstFunctionCall(ctx));
            getPositionFrom(param, ctx);
            return param;
        }
        if (ctx.constructor() != null) {
            ActualParam param = new ActualParam(toAst(ctx.constructor()));
            getPositionFrom(param, ctx);
            return param;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private Expression toAst(OguParser.Func_nameContext ctx) {
        Reference id = new Reference(new OguIdentifier(idText(ctx.name)));
        getPositionFrom(id, ctx);
        return id;
    }

    private Expression toAst(OguParser.Qual_func_nameContext ctx) {
        if (ctx.name == null) {
            OguTypeIdentifier tname = OguTypeIdentifier.create(ctx.qual.stream().map(Token::getText).collect(Collectors.toList()));
            TypeReference ref = new TypeReference(tname);
            getPositionFrom(ref, ctx);
            return ref;
        } else {
            OguIdentifier tname = OguIdentifier.create(ctx.qual.stream().map(Token::getText).collect(Collectors.toList()), idText(ctx.name));
            Reference id = new Reference(tname);
            getPositionFrom(id, ctx);
            return id;
        }
    }


    private Expression toAst(OguParser.AtomContext ctx) {
        if (ctx.string_literal != null) {
            StringLiteral lit = new StringLiteral(idText(ctx.STRING().getSymbol()));
            getPositionFrom(lit, ctx);
            return lit;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionCall toAstFunctionCall(OguParser.ExprContext ctx) {
        if (ctx.function != null) {
            Expression function = toAst(ctx.function);
            FunctionCall functionCall = new FunctionCall(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
            getPositionFrom(functionCall, ctx);
            return functionCall;

        }
        if (ctx.qual_function != null) {
            Expression function = toAst(ctx.qual_function);
            FunctionCall functionCall = new FunctionCall(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
            getPositionFrom(functionCall, ctx);
            return functionCall;
        }

        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }


    private String idText(Token token) {
        return token.getText();
    }

    private String buildModuleNameFromFileName(String name) {
        int pos = name.indexOf('.');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1, pos);
    }

}
