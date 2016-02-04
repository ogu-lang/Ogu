package org.ogu.lang.parser;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.ogu.lang.antlr.OguParser;
import org.ogu.lang.parser.ast.*;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.decls.funcdef.*;
import org.ogu.lang.parser.ast.decls.typedef.ClassParam;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamConstrained;
import org.ogu.lang.parser.ast.expressions.*;
import org.ogu.lang.parser.ast.expressions.control.*;
import org.ogu.lang.parser.ast.expressions.literals.*;
import org.ogu.lang.parser.ast.modules.*;
import org.ogu.lang.parser.ast.typeusage.*;
import org.ogu.lang.util.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    public ModuleNode toAst(File file, org.ogu.lang.antlr.OguParser.ModuleContext ctx) {
        ModuleNode module = new ModuleNode();
        getPositionFrom(module, ctx);
        module.setName(toAst(file, ctx.moduleHeader));

        OguParser.Module_bodyContext bodyCtx = ctx.module_body();
        for (OguParser.Module_declContext memberCtx : bodyCtx.module_decl()) {
            Node memberNode = toAst(memberCtx);
            if (memberNode instanceof ExpressionNode)
                module.add((ExpressionNode) memberNode);
            else if (memberNode instanceof AliasDeclaration)
                module.add((AliasDeclaration) memberNode);
            else if (memberNode instanceof ExportableDeclaration)
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

    private ModuleNameNode toAst(File file, OguParser.Module_headerContext ctx) {
        if (ctx == null)
            return new ModuleNameNode(buildModuleNameFromFileName(file.getName()));
        return new ModuleNameNode(toAst(ctx.name).qualifiedName());
    }

    private TypeIdentifierNode toAst(OguParser.Module_nameContext ctx) {
        TypeIdentifierNode type = TypeIdentifierNode.create(ctx.parts.stream().map(Token::getText).collect(Collectors.toList()));
        getPositionFrom(type, ctx);
        return type;
    }

    private TypeIdentifierNode toOguTypeIdentifier(OguParser.Alias_targetContext ctx) {
        TypeIdentifierNode tname = TypeIdentifierNode.create(idText(ctx.alias_tid));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private TypeIdentifierNode toOguTypeIdentifier(OguParser.Alias_originContext ctx) {
        TypeIdentifierNode tname = TypeIdentifierNode.create(ctx.alias_origin_tid.stream().map(Token::getText).collect(Collectors.toList()));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private IdentifierNode toOguIdentifier(OguParser.Alias_targetContext ctx) {
        IdentifierNode tname = IdentifierNode.create(idText(ctx.alias_id));
        getPositionFrom(tname, ctx);
        return tname;
    }


    private IdentifierNode toOguIdentifier(OguParser.Alias_originContext ctx) {
        IdentifierNode tname = IdentifierNode.create(ctx.alias_origin_tid.stream().map(Token::getText).collect(Collectors.toList()), idText(ctx.alias_origin_id));
        getPositionFrom(tname, ctx);
        return tname;
    }

    private ExportsDeclaration toAst(OguParser.Export_nameContext ctx, List<Decorator> decs) {
        ExportsDeclaration result;
        if (ctx.ID() != null)
            result = new ExportsFunctionDeclaration(new IdentifierNode(idText(ctx.ID().getSymbol())), decs);
        else if (ctx.TID() != null)
            result = new ExportsTypeDeclaration(TypeIdentifierNode.create(idText(ctx.TID().getSymbol())), decs);
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

        if (ctx.var() != null) {
            return toAst(ctx.var(), decs);
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

        if (ctx.instance_def() != null) {
            return toAst(ctx.instance_def(), decs);
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


        Logger.debug(ctx.getText());

        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private InstanceDeclaration toAst(OguParser.Instance_defContext ctx, List<Decorator> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParam> params = getInstaceTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclaration> members = internalDeclToAst(ctx.internal_decl());
        InstanceDeclaration instance = new InstanceDeclaration(name, params, members, decs);
        getPositionFrom(instance, ctx);
        return instance;
    }

    private List<TypeParam> getInstaceTypeParamList(OguParser.Instance_defContext ctx, List<OguParser.TypeContext> types, Map<String, TypeNode> constraints) {
        List<TypeParam> params = new ArrayList<>();
        for (OguParser.TypeContext typeContext : types) {
            TypeNode type = toAst(typeContext);
            TypeParam p = new TypeParam(type);
            getPositionFrom(p, ctx);
            params.add(p);
        }
        return params;

    }

    private ClassDeclaration toAst(OguParser.Class_defContext ctx, List<Decorator> decs) {
        boolean isMutable = ctx.mut != null;
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParam> genParams = getTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclaration> members = internalDeclToAst(ctx.internal_decl());
        List<ClassParam> params = new ArrayList<>();
        if (ctx.class_params() != null)
            params = toAst(ctx.class_params());
        ClassDeclaration clazz = new ClassDeclaration(name, isMutable, genParams, params, members, decs);
        getPositionFrom(clazz, ctx);
        return clazz;
    }

    private List<ClassParam> toAst(OguParser.Class_paramsContext ctx) {
        List<ClassParam> result = new ArrayList<>();
        for (OguParser.Class_paramContext cp:ctx.class_param()) {
            result.addAll(toAst(cp));
        }
        return result;
    }

    private List<ClassParam> toAst(OguParser.Class_paramContext ctx) {
        TypeNode type = toAst(ctx.type());
        List<ClassParam> result = new ArrayList<>();
        for (TerminalNode cid:ctx.ID()) {
            IdentifierNode id = IdentifierNode.create(cid.getText());
            result.add(new ClassParam(id, type));
        }
        return result;
    }


    private EnumDeclaration toAst(OguParser.Enum_defContext ctx, List<Decorator> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(ctx.en.getText());
        List<IdentifierNode> values = ctx.values.stream().map((t) -> new IdentifierNode(idText(t))).collect(Collectors.toList());
        List<TypeIdentifierNode> deriving = toDerivingAst(ctx.deriving());
        EnumDeclaration decl = new EnumDeclaration(name, values, deriving, decs);
        getPositionFrom(decl, ctx);
        return decl;
    }

    private List<TypeIdentifierNode> toDerivingAst(OguParser.DerivingContext ctx) {
        if (ctx == null)
            return Collections.emptyList();

        List<TypeIdentifierNode> deriving = ctx.deriving_types().dt.stream().map(this::toAst).collect(Collectors.toList());
        for (TypeIdentifierNode oguTypeIdentifier : deriving) {
           getPositionFrom(oguTypeIdentifier, ctx);
        }
        return deriving;
    }

    private DataDeclaration toAst(OguParser.Data_defContext ctx, List<Decorator> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String, TypeNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);

        List<TypeParam> params;
        if (ctx.typedef_params() != null)
            params = getTypeParamList(ctx, ctx.typedef_params().params, constraints);
        else
            params = Collections.emptyList();
        List<TypeIdentifierNode> deriving = new ArrayList<>();
        List<TypeNode> values = toAst(ctx.data_type_decl(), deriving);
        DataDeclaration decl = new DataDeclaration(name, params, values, deriving, decs);
        getPositionFrom(decl, ctx);
        return decl;
    }

    private List<TypeNode> toAst(OguParser.Data_type_declContext ctx, List<TypeIdentifierNode> deriving) {
        if (ctx.deriving() != null)
            deriving.addAll(toDerivingAst(ctx.deriving()));
        List<TypeNode> types = new ArrayList<>();
        for (OguParser.TypeContext t : ctx.t) {
            types.add(toAst(t));
        }
        return types;
    }

    private TraitDeclaration toAst(OguParser.Trait_defContext ctx, List<Decorator> decs) {
        boolean isMutable = ctx.mut != null;
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParam> params = getTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclaration> members = internalDeclToAst(ctx.internal_decl());
        TraitDeclaration trait = new TraitDeclaration(name, isMutable, params, members, decs);
        getPositionFrom(trait, ctx);
        return trait;
    }

    private List<FunctionalDeclaration> internalDeclToAst(List<OguParser.Internal_declContext> decls) {
        List<FunctionalDeclaration> members = new ArrayList<>();
        for (OguParser.Internal_declContext decl : decls) {
            members.add(toAst(decl));
        }
        return members;
    }

    private FunctionalDeclaration toAst(OguParser.Internal_declContext ctx) {
        List<Decorator> decs = toAstDecorators(ctx.decorators());

        if (ctx.func_decl() != null)
            return toAst(ctx.func_decl(), decs);
        if (ctx.val_def() != null)
            return toAst(ctx.val_def(), decs);
        if (ctx.func_def() != null)
            return toAst(ctx.func_def(), decs);
        if (ctx.var() != null)
            return toAst(ctx.var(), decs);
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TypedefDeclaration toAst(OguParser.Type_defContext ctx, List<Decorator> decs) {
        Map<String,TypeNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);


        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.t));
        TypeNode type = toAst(ctx.type());
        getPositionFrom(name, ctx);
        if (ctx.ta == null)  {
            SimpleTypeDeclaration tdecl = new SimpleTypeDeclaration(name, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        } else {
            // TODO Warning if are unused contraints
            List<TypeParam> params = getTypeParamList(ctx, ctx.typedef_params().params, constraints);
            GenericTypeDeclaration tdecl = new GenericTypeDeclaration(name, params, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        }
    }

    private List<TypeParam> getTypeParamList(ParserRuleContext ctx, List<Token> tokens, Map<String,TypeNode> constraints) {
        if (ctx == null)
            return Collections.emptyList();

        List<TypeParam> params = new ArrayList<>();
        for (Token token : tokens) {
            String id = idText(token);
            TypeParam p;
            if (constraints.containsKey(id))
                p = new TypeParamConstrained(id, constraints.get(id));
            else
                p = new TypeParam(id);
            getPositionFrom(p, ctx);
            params.add(p);
        }
        return params;
    }

    private void loadTypeConstraints(OguParser.Typedef_args_constraintsContext ctx, Map<String, TypeNode> cons) {
        for (OguParser.Typedef_arg_constraintContext tac:ctx.tac) {
            TypeNode type = toAst(tac.type());
            for (Token id : tac.ids) {
                cons.put(idText(id), type);
            }
        }
    }

    private FunctionalDeclaration toAst(OguParser.Func_defContext ctx, List<Decorator> decorators) {
        if (ctx.let_func_name != null) {
            if (ctx.let_func_name.lid_val_id != null) {
                IdentifierNode funcId = IdentifierNode.create(idText(ctx.let_func_name.lid_val_id));
                TypeNode type = toAst(ctx.let_func_name.t);
                if (ctx.let_func_args != null && !ctx.let_func_args.isEmpty()) {
                    return new ErrorFunctionalDeclaration("error.let_as_val.no_params", getPosition(ctx));
                }
                ExpressionNode expr = toAst(ctx.let_expr());
                ValDeclaration val = new ValDeclaration(funcId, type, expr, decorators);
                getPositionFrom(val, ctx);
                return val;
            }
            if (ctx.let_func_name.lid_fun_id != null) {
                IdentifierNode funcId = IdentifierNode.create(idText(ctx.let_func_name.lid_fun_id));
                List<FunctionPatternParam> params = funcArgsToAst(ctx.let_func_args);
                LetDefinition funcdef = new LetDefinition(funcId, params, decorators);
                getPositionFrom(funcdef, ctx);
                if (ctx.let_expr() != null) {
                    toAst(ctx.let_expr(), funcdef);

                }
                return funcdef;
            }
        }
        if (ctx.infix_op != null || ctx.prefix_op != null) {
            List<FunctionPatternParam> params = new ArrayList<>();
            params.add(toAst(ctx.left));
            params.add(toAst(ctx.right));
            OperatorNode op = ctx.infix_op != null ? toAst(ctx.infix_op) : toAst(ctx.prefix_op);
            OpDefinition opDef = new OpDefinition(op, params, decorators);
            getPositionFrom(opDef, ctx);
            if (ctx.let_expr() != null) {
                toAst(ctx.let_expr(), opDef);
            }
            return opDef;
        }



        if (!ctx.tup.isEmpty()) {
            List<IdentifierNode> ids = new ArrayList<>();
            Map<IdentifierNode, TypeNode> types = new HashMap<>();
            lidsToAst(ctx.tup, ids, types);
            ExpressionNode value = toAst(ctx.expr());
            TupleValDeclaration decl = new TupleValDeclaration(ids, types, value, decorators);
            getPositionFrom(decl, ctx);
            return decl;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void lidsToAst(List<OguParser.LidContext> lids, List<IdentifierNode> ids, Map<IdentifierNode, TypeNode> types) {
        for(OguParser.LidContext ctx : lids) {
            if (ctx.lid_fun_id !=null) {
                ids.add(IdentifierNode.create(idText(ctx.lid_fun_id)));
            } else {
                IdentifierNode id = IdentifierNode.create(idText(ctx.lid_val_id));
                TypeNode type = toAst(ctx.type());
                ids.add(id);
                types.put(id, type);
            }
        }
    }

    private ExpressionNode toAst(OguParser.Let_exprContext ctx) {
        if (ctx.expr() != null) {
            return toAst(ctx.expr());
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void toAst(OguParser.Let_exprContext ctx, LetDeclaration funcdef) {
        if (ctx.let_block() != null) {
            toAst(ctx.let_block(), funcdef);
            if (ctx.let_block().where() != null)
                parseWhere(ctx.let_block().where(), funcdef);
            return;
        }
        if (ctx.expr() != null) {
            funcdef.add(toAst(ctx.expr()));
            if (ctx.where() != null)
                parseWhere(ctx.where(), funcdef);
            return;
        }
        if (ctx.guards() != null) {
            for (OguParser.GuardContext guard:ctx.guards().guard()) {
                funcdef.add(toAst(guard));
            }
            if (ctx.where() != null)
                parseWhere(ctx.where(), funcdef);
            if (ctx.guards().where() != null)
                parseWhere(ctx.guards().where(), funcdef);
            return;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void parseWhere(OguParser.WhereContext ctx, LetDeclaration funcdef) {
        for (OguParser.Where_exprContext wc:ctx.wl) {
            parseWhereCtx(wc, funcdef);
        }
    }

    private void parseWhereCtx(OguParser.Where_exprContext ctx, LetDeclaration funcdef) {
        if (ctx.i != null) {
            IdentifierNode funcId = IdentifierNode.create(idText(ctx.i));
            List<FunctionPatternParam> params = funcArgsToAst(ctx.let_arg());
            LetDefinition letDef = new LetDefinition(funcId, params, Collections.emptyList());
            getPositionFrom(letDef, ctx);
            if (ctx.let_expr() != null) {
                toAst(ctx.let_expr(), letDef);
            }
            WhereDeclaration where = new WhereDeclaration(letDef);
            getPositionFrom(where, ctx);
            funcdef.add(where);
            return;
        }
        else {
            if (!ctx.tup.isEmpty()) {
                List<IdentifierNode> ids = new ArrayList<>();
                Map<IdentifierNode, TypeNode> types = new HashMap<>();
                lidsToAst(ctx.tup, ids, types);
                ExpressionNode value = toAst(ctx.expr());
                TupleValDeclaration decl = new TupleValDeclaration(ids, types, value, Collections.emptyList());
                getPositionFrom(decl, ctx);
                WhereDeclaration where = new WhereDeclaration(decl);
                getPositionFrom(where, ctx);
                funcdef.add(where);
                return;
            }

        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private GuardDeclaration toAst(OguParser.GuardContext ctx) {
        ExpressionNode base = toAst(ctx.be);
        List<ExpressionNode> args = new ArrayList<>();
        for (OguParser.ExprContext c : ctx.ae)
            args.add(toAst(c));
        List<ExpressionNode> exprs = new ArrayList<>();
        if (ctx.de != null)
            exprs.add(toAst(ctx.de));
        else {
            iterLetDecls(ctx.eb.let_decl(), exprs);
        }
        DoExpressionNode doExpr = new DoExpressionNode(exprs);
        getPositionFrom(doExpr, ctx);
        GuardDeclaration guard = new GuardDeclaration(base, args, doExpr);
        getPositionFrom(guard, ctx);
        return guard;
    }

    private void toAst(OguParser.Let_blockContext ctx, LetDeclaration funcdef) {
        for (OguParser.Let_declContext decl : ctx.ld) {
            if (decl.expr() != null)
                funcdef.add(toAst(decl.expr()));
            else if (decl.func_def() != null)
                funcdef.add(new FunctionNodeDecl(toAst(decl.func_def(),Collections.emptyList())));
            else { ///var
                funcdef.add(new FunctionNodeDecl(toAst(decl.var(), Collections.emptyList())));
            }
        }
    }


    private List<FunctionPatternParam> funcArgsToAst(List<OguParser.Let_argContext> let_func_args) {
        return let_func_args.stream().map(this::toAst).collect(Collectors.toList());
    }

    private FunctionPatternParam toAst(OguParser.Let_argContext ctx) {


        if (ctx.l_atom != null)
            return toAst(ctx.l_atom);

        if (ctx.let_arg_vector() != null)
            return toAst(ctx.let_arg_vector());


        if (ctx.let_arg_tuple_or_list() != null)
            return toAst(ctx.let_arg_tuple_or_list());

        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionPatternParam toAst(OguParser.Let_arg_tuple_or_listContext ctx) {
        if (ctx.la != null && !ctx.la.isEmpty()) {
            List<FunctionPatternParam> args = new ArrayList<>();
            for (OguParser.Let_arg_atomContext ac:ctx.la)
                args.add(toAst(ac));
            FuncListParam param = new FuncListParam(args);
            getPositionFrom(param, ctx);
            return param;
        }
        if (ctx.ta != null && !ctx.ta.isEmpty()) {
            List<FunctionPatternParam> args = new ArrayList<>();
            for (OguParser.Let_arg_atomContext ac:ctx.ta)
                args.add(toAst(ac));
            FuncTupleParam param = new FuncTupleParam(args);
            getPositionFrom(param, ctx);
            return param;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionPatternParam toAst(OguParser.Let_arg_vectorContext ctx) {
        if (ctx.la == null || ctx.la.isEmpty()) {
            FuncEmptyVectorParam param = new FuncEmptyVectorParam();
            getPositionFrom(param, ctx);
            return param;
        }
        List<FunctionPatternParam> args = new ArrayList<>();
        for (OguParser.Let_arg_atomContext ac:ctx.la) {
             args.add(toAst(ac));
        }
        FuncVectorParam param = new FuncVectorParam(args);
        getPositionFrom(param, ctx);
        return param;
    }

    private FunctionPatternParam toAst(OguParser.Let_arg_atomContext ctx) {
        if (ctx.l_id != null) {
            if (ctx.l_id.lid_fun_id != null) {
                FuncIdParam id = toAst(ctx.l_id.lid_fun_id);
                getPositionFrom(id, ctx);
                return id;
            }
            else {
                TypeNode type = toAst(ctx.l_id.type());
                IdentifierNode id = IdentifierNode.create(idText(ctx.l_id.lid_val_id));
                FuncIdTypeParam param = new FuncIdTypeParam(id, type);
                getPositionFrom(param, ctx);
                return param;
            }
        }
        if (ctx.t_id != null)
        {
            TypeIdentifierNode tid =  TypeIdentifierNode.create(idText(ctx.t_id));
            if (ctx.la == null) {
                FuncTypeParam typeParam = new FuncTypeParam(tid);
                getPositionFrom(typeParam, ctx);
                return typeParam;
            } else {
                List<FunctionPatternParam> args = new ArrayList<>();
                for (OguParser.Let_argContext ac:ctx.la)
                    args.add(toAst(ac));
                FuncGenericTypeParam typeParam = new FuncGenericTypeParam(tid, args);
                getPositionFrom(typeParam, ctx);
                return typeParam;
            }
        }

        if (ctx.a != null) {
            FuncExprParam param = new FuncExprParam(toAst(ctx.atom()));
            getPositionFrom(param, ctx);
            return param;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FuncIdParam toAst(Token tok) {
        return new FuncIdParam(IdentifierNode.create(idText(tok)));
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
        if ("primitive".equals(decoratorId)) {
            Decorator decorator = new PrimitiveDecorator();
            getPositionFrom(decorator, ctx);
            return decorator;
        }
        if ("entrypoint".equals(decoratorId)) {
            Decorator decorator = new EntryPointDecorator();
            getPositionFrom(decorator, ctx);
            return decorator;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionalDeclaration toAst(OguParser.Func_declContext ctx, List<Decorator> decorators) {
        if (ctx.name.f_id != null) {
            List<TypeArgNode> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            FunctionDeclaration funcDecl = new FunctionDeclaration(toAst(ctx.name), params, decorators);
            getPositionFrom(funcDecl, ctx);
            return funcDecl;
        }
        if (ctx.name.f_op != null) {
            // op params...
            List<TypeArgNode> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            OpDeclaration opDecl = new OpDeclaration(toAst(ctx.name.f_op), params, decorators);
            getPositionFrom(opDecl, ctx);
            return opDecl;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private OperatorNode toAst(OguParser.OpContext ctx) {
        OperatorNode op = new OperatorNode(ctx.getText());
        getPositionFrom(op, ctx);
        return op;
    }

    private TypeArgNode toAst(OguParser.Func_decl_argContext ctx) {
        if (ctx.unit() != null)
            return new UnitTypeArgNode();
        if (ctx.fda_id != null)
            return new QualifiedTypeArgNode(TypeIdentifierNode.create(idText(ctx.fda_id)));
        if (!ctx.fda_tid.isEmpty()) {
            TypeIdentifierNode id = TypeIdentifierNode.create(ctx.fda_tid.stream().map(this::idText).collect(Collectors.toList()));
            if (ctx.tid_or_id_arg == null || ctx.tid_or_id_arg.isEmpty()){
                return new QualifiedTypeArgNode(id);
            } else {
                List<NameNode> args = new ArrayList<>();
                for (OguParser.Tid_or_idContext ti:ctx.tid_or_id_arg)
                    if (ti.i != null)
                        args.add(IdentifierNode.create(idText(ti.i)));
                    else
                        args.add(TypeIdentifierNode.create(idText(ti.t)));
                GenericTypeArgNode genType = new GenericTypeArgNode(id, args);
                getPositionFrom(genType, ctx);
                return genType;
            }
        }
        if (ctx.vector() != null) {
            TypeArgNode arg = toAst(ctx.vector().func_decl_arg());
            VectorTypeArgNode vec = new VectorTypeArgNode(arg);
            getPositionFrom(vec, ctx);
            return vec;
        }
        if (ctx.tuple() != null) {
            List<TypeArgNode> args = new ArrayList<>();
            for (OguParser.Func_decl_argContext ct:ctx.tuple().func_decl_arg())
                args.add(toAst(ct));
            TupleTypeArgNode tuple = new TupleTypeArgNode(args);
            getPositionFrom(tuple, ctx);
            return tuple;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private IdentifierNode toAst(OguParser.Func_name_declContext ctx) {
        if (ctx.f_id != null)
            return new IdentifierNode(idText(ctx.f_id));
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
                return new ErrorAlias(message("error.alias.tid_no_tid"), getPosition(ctx));
            }
            TypeAliasDeclaration decl = new TypeAliasDeclaration(toOguTypeIdentifier(target), toOguTypeIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        } else {
            if (origin.alias_origin_id == null) {
                return new ErrorAlias(message("error.alias.id_no_id"), getPosition(ctx));
            }
            IdAliasDeclaration decl = new IdAliasDeclaration(toOguIdentifier(target), toOguIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        }
    }


    private VarDeclaration toAst(OguParser.VarContext ctx, List<Decorator> decs) {
        if (ctx.vid().i != null) {
            IdentifierNode id =  IdentifierNode.create(idText(ctx.vid().i));
            VarDeclaration var;
            if (ctx.type() == null) {
                var = new VarDeclaration(id, toAst(ctx.expr()), decs);
            } else {
                if (ctx.expr() != null)
                    var = new VarDeclaration(id, toAst(ctx.type()), toAst(ctx.expr()), decs);
                else
                    var = new VarDeclaration(id, toAst(ctx.type()), decs);
            }
            getPositionFrom(var, ctx);
            return var;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private ValDeclaration toAst(OguParser.Val_defContext ctx, List<Decorator> decorators) {
        if (ctx.val_id != null) {
            if (ctx.type() == null) {
                ValDeclaration val = new ValDeclaration(IdentifierNode.create(idText(ctx.val_id)), toAst(ctx.expr()), decorators);
                getPositionFrom(val, ctx);
                return val;
            }
            ValDeclaration val = new ValDeclaration(IdentifierNode.create(idText(ctx.val_id)), toAst(ctx.type()), toAst(ctx.expr()), decorators);
            getPositionFrom(val, ctx);
            return val;
        }
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TypeNode toAst(OguParser.TypeContext ctx) {
        if (ctx.vector_type() != null) {
            TypeNode type = toAst(ctx.vector_type().type());
            VectorTypeNode vtype = new VectorTypeNode(type);
            getPositionFrom(vtype, ctx);
            return vtype;
        }
        if (ctx.unit() != null) {
            UnitTypeNode utype = new UnitTypeNode();
            getPositionFrom(utype, ctx);
            return utype;
        }
        if (ctx.tuple_type() != null) {
            List<TypeNode> types = new ArrayList<>();
            for (OguParser.TypeContext type : ctx.tuple_type().type()) {
                types.add(toAst(type));
            }
            TupleTypeNode ttype = new TupleTypeNode(types);
            getPositionFrom(ttype, ctx);
            return ttype;
        }
        if (ctx.map_type() != null) {
            TypeNode key = toAst(ctx.map_type().k);
            TypeNode val = toAst(ctx.map_type().v);
            MapTypeNode type = new MapTypeNode(key, val);
            getPositionFrom(type, ctx);
            return type;
        }

        if (ctx.anon_record_type() != null) {
            return toAst(ctx.anon_record_type());
        }

        if (ctx.record_type() != null) {
            return toAst(ctx.record_type());
        }

        if (ctx.tid() != null) {
            if (ctx.t_a.isEmpty()) {
                return new QualifiedTypeArgNode(toAst(ctx.tid()));
            } else {
                List<TypeNode> args = new ArrayList<>();
                for (OguParser.Tid_argsContext ac:ctx.t_a)
                    args.add(toAst(ac));
                TypeIdentifierNode tName = toAst(ctx.gt);
                GenericTypeNode type = new GenericTypeNode(tName, args);
                getPositionFrom(type, ctx);
                return type;
            }
        }
        if (ctx.nat != null) {
            NativeTypeNode type = new NativeTypeNode(idText(ctx.nat));
            getPositionFrom(type, ctx);
            return type;
        }
        if (ctx.i != null) {
            IdTypeArgNode type = new IdTypeArgNode(IdentifierNode.create(idText(ctx.i)));
            getPositionFrom(type, ctx);
            return type;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }



    private TypeNode toAst(OguParser.Tid_argsContext ctx) {
        if (ctx.i != null) {
            IdTypeArgNode type = new IdTypeArgNode(IdentifierNode.create(idText(ctx.i)));
            getPositionFrom(type, ctx);
            return type;
        }
        if (ctx.type() != null) {
            return toAst(ctx.type());
        }
        if (ctx.tid() != null) {
            QualifiedTypeNode type = new QualifiedTypeNode(toAst(ctx.tid()));
            getPositionFrom(type, ctx);
            return type;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private RecordTypeNode toAst(OguParser.Record_typeContext ctx) {
        List<RecordFieldNode> fields = new ArrayList<>();
        for (OguParser.FldDeclContext fc:ctx.fldDecl()) {
            fields.add(toAst(fc));
        }
        TypeIdentifierNode name = TypeIdentifierNode.create(idText(ctx.ti));
        RecordTypeNode record = new RecordTypeNode(name, fields);
        getPositionFrom(record, ctx);
        return record;
    }

    private AnonRecordTypeNode toAst(OguParser.Anon_record_typeContext ctx) {
        List<RecordFieldNode> fields = new ArrayList<>();
        for (OguParser.FldDeclContext fc:ctx.fldDecl()) {
            fields.add(toAst(fc));
        }
        AnonRecordTypeNode record = new AnonRecordTypeNode(fields);
        getPositionFrom(record, ctx);
        return record;
    }

    private RecordFieldNode toAst(OguParser.FldDeclContext ctx) {
        IdentifierNode id = IdentifierNode.create(idText(ctx.i));
        TypeNode type = toAst(ctx.t);
        RecordFieldNode fld = new RecordFieldNode(id, type);
        getPositionFrom(fld, ctx);
        return fld;
    }

    private TypeIdentifierNode toAst(OguParser.TidContext ctx) {
        return TypeIdentifierNode.create(ctx.t.stream().map(this::idText).collect(Collectors.toList()));
    }


    private ExpressionNode toAst(OguParser.ExprContext ctx) {

        if (ctx.if_expr() != null) {
            return toAst(ctx.if_expr());
        }

        if (ctx.for_expr() != null) {
            return toAst(ctx.for_expr());
        }

        if (ctx.case_expr() != null) {
            return toAst(ctx.case_expr());
        }

        if (ctx.when_expr() != null) {
            return toAst(ctx.when_expr());
        }

        if (ctx.while_expr() != null) {
            return toAst(ctx.while_expr());
        }

        if (ctx.let_in_expr() != null) {
            return toAst(ctx.let_in_expr());
        }

        if (ctx.lambda_expr() != null) {
            return toAst(ctx.lambda_expr());
        }

        if (ctx.yield_expr() != null) {
            return toAst(ctx.yield_expr());
        }

        if (ctx.recur_expr() != null) {
            return toAst(ctx.recur_expr());
        }


        if (ctx.assign_expr() != null) {
            return toAst(ctx.assign_expr());
        }

        if (ctx.paren_expr() != null) {
            return toAst(ctx.paren_expr());
        }

        if (ctx.vector_expr() != null) {
            return toAst(ctx.vector_expr());
        }

        if (ctx.constructor() != null) {
            return toAst(ctx.constructor());
        }

        if (ctx.infix_id != null) {
            ReferenceNode name = new ReferenceNode(IdentifierNode.create(idText(ctx.infix_id)));
            ExpressionNode l = toAst(ctx.l_infix);
            ExpressionNode r = toAst(ctx.r_infix);
            List<ActualParam> params = new ArrayList<>();
            params.add(new ActualParam(l));
            params.add(new ActualParam(r));
            FunctionCallNode call = new FunctionCallNode(l, params);
            getPositionFrom(call, ctx);
            return call;
        }

        if (ctx.self_id() != null) {
            return toAst(ctx.self_id());
        }

        if (ctx.function != null) {
            return toAstFunctionCall(ctx);
        }


        if (ctx.qual_function != null) {
            return toAstFunctionCall(ctx);
        }
        if (ctx.ref != null) {
            ReferenceNode ref = new ReferenceNode(IdentifierNode.create(idText(ctx.ref)));
            getPositionFrom(ref, ctx);
            return ref;
        }
        if (ctx.o != null) {
            ExpressionNode left = toAst(ctx.l);
            ExpressionNode right = toAst(ctx.r);
            BinaryOpExpressionNode expr = new BinaryOpExpressionNode(new OperatorNode(ctx.o.getText()), left, right);
            getPositionFrom(expr, ctx);
            return expr;
        }
        if (ctx.primary() != null) {
            return toAst(ctx.primary());
        }

        if (ctx.dict_expr() != null) {
            return toAst(ctx.dict_expr());
        }

        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private SelfRefExpressionNode toAst(OguParser.Self_idContext ctx) {
        SelfRefExpressionNode expr = new SelfRefExpressionNode(IdentifierNode.create(idText(ctx.i)));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private DictExpressionNode toAst(OguParser.Dict_exprContext ctx) {
        if (ctx.map_expr() == null) {
            EmptyMapExpressionNode dic = new EmptyMapExpressionNode();
            getPositionFrom(dic, ctx);
            return dic;
        }
        if (ctx.map_expr().m_arrow != null && !ctx.map_expr().ma.isEmpty()) {
            List<ExpressionNode> keys = new ArrayList<>();
            List<ExpressionNode> vals = new ArrayList<>();
            for (OguParser.M_arrowContext mc:ctx.map_expr().ma) {
                keys.add(toAst(mc.k));
                vals.add(toAst(mc.v));
            }
            MapExpressionNode map = new MapExpressionNode(keys, vals);
            getPositionFrom(map, ctx);
            return map;
        }
        if (ctx.map_expr().m_assign != null && !ctx.map_expr().mb.isEmpty()) {
            List<FieldExpressionNode> fieldExprs = new ArrayList<>();
            for (OguParser.M_assignContext mc:ctx.map_expr().mb) {
                IdentifierNode id = IdentifierNode.create(idText(mc.i));
                ExpressionNode expr = toAst(mc.expr());
                FieldExpressionNode fld = new FieldExpressionNode(id, expr);
                getPositionFrom(fld, ctx.map_expr());
                fieldExprs.add(fld);
            }
            RecordExpressionNode rec = new RecordExpressionNode(fieldExprs);
            getPositionFrom(rec, ctx);
            return rec;
        }

        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private LetInExpressionNode toAst(OguParser.Let_in_exprContext ctx) {
        Map<IdentifierNode, ExpressionNode> exprs = new HashMap<>();
        for (OguParser.Let_in_argContext lin:ctx.let_in_arg()) {
            IdentifierNode id = IdentifierNode.create(idText(lin.i));
            ExpressionNode expr = toAst(lin.e);
            exprs.put(id, expr);
        }
        LetInExpressionNode expr;
        if (ctx.in_expr == null)
            expr = new LetInExpressionNode(exprs);
        else
            expr = new LetInExpressionNode(exprs, toAst(ctx.in_expr));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private RecurExpressionNode toAst(OguParser.Recur_exprContext ctx) {
        List<ExpressionNode> args = new ArrayList<>();
        for (OguParser.ExprContext ce:ctx.expr())
            args.add(toAst(ce));
        RecurExpressionNode expr = new RecurExpressionNode(args);
        getPositionFrom(expr, ctx);
        return expr;
    }

    private YieldExpressionNode toAst(OguParser.Yield_exprContext ctx) {
        YieldExpressionNode expr = new YieldExpressionNode(toAst(ctx.expr()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private LambdaExpressionNode toAst(OguParser.Lambda_exprContext ctx) {
        List<LambdaArgNode> args = new ArrayList<>();
        if (ctx.lambda_args() != null) {
            for (OguParser.Lambda_argContext cl:ctx.lambda_args().lambda_arg()) {
                args.addAll(toAst(cl));
            }
        }
        DoExpressionNode doExpr;
        if (ctx.expr() == null)
            doExpr = toAst(ctx.block());
        else
            doExpr = new DoExpressionNode(ImmutableList.of(toAst(ctx.expr())));
        LambdaExpressionNode expr = new LambdaExpressionNode(args, doExpr);
        getPositionFrom(expr, ctx);
        return expr;
    }

    private DoExpressionNode toAst(OguParser.BlockContext ctx) {
        List<ExpressionNode> exprs = new ArrayList<>();
        iterLetDecls(ctx.let_decl(), exprs);
        DoExpressionNode doExpr = new DoExpressionNode(exprs);
        getPositionFrom(doExpr, ctx);
        return doExpr;
    }

    private List<LambdaArgNode> toAst(OguParser.Lambda_argContext ctx) {
        if (ctx.i != null) {
            LambdaArgNode arg;
            IdentifierNode id = IdentifierNode.create(idText(ctx.i));
            if (ctx.type() == null)
                arg = new LambdaArgNode(id);
            else
                arg = new LambdaArgNode(id, toAst(ctx.type()));
            getPositionFrom(arg, ctx);
            return ImmutableList.of(arg);
        }
        List<LambdaArgNode> args = new ArrayList<>();
        for (OguParser.Lambda_argContext lac:ctx.lambda_arg()) {
            args.addAll(toAst(lac));
        }
        return args;
    }

    private WhileExpressionNode toAst(OguParser.While_exprContext ctx) {
        WhileExpressionNode expr = new WhileExpressionNode(toAst(ctx.expr()), toAst(ctx.do_expression()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private WhenExpressionNode toAst(OguParser.When_exprContext ctx) {
        WhenExpressionNode expr = new WhenExpressionNode(toAst(ctx.expr()), toAst(ctx.do_expression()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private AssignExpressionNode toAst(OguParser.Assign_exprContext ctx) {
        AssignExpressionNode expr;
        if (ctx.si != null) {
            IdentifierNode id = IdentifierNode.create(idText(ctx.si));
            if (ctx.a == null)
                expr = new AssignSelfExpressionNode(id, toAst(ctx.e));
            else
                expr = new AssignSelfExpressionNode(id, toAst(ctx.a), toAst(ctx.e));
        } else {
            IdentifierNode id = IdentifierNode.create(idText(ctx.i));
            if (ctx.a == null)
                expr = new AssignExpressionNode(id, toAst(ctx.e));
            else
                expr = new AssignExpressionNode(id, toAst(ctx.a), toAst(ctx.e));
        }
        getPositionFrom(expr, ctx);
        return expr;
    }

    private ExpressionNode toAst(OguParser.For_exprContext ctx) {
        SetConstraintNode forCond = toAst(ctx.set_constraint_expr());
        DoExpressionNode doExpr = toAst(ctx.do_expression());
        ForExpressionNode forExpr = new ForExpressionNode(forCond, doExpr);
        getPositionFrom(forExpr, ctx);
        return forExpr;
    }

    private DoExpressionNode toAst(OguParser.Do_expressionContext ctx) {
        List<ExpressionNode> exprs = new ArrayList<>();
        if (ctx.block() == null) {
            exprs.add(toAst(ctx.expr()));
        } else {
            iterLetDecls(ctx.block().let_decl(), exprs);
        }
        DoExpressionNode doExpr = new DoExpressionNode(exprs);
        getPositionFrom(doExpr, ctx);
        return doExpr;
    }

    private VarDeclExpressionNode toAst(OguParser.VarContext ctx) {
        VarDeclExpressionNode expr = new VarDeclExpressionNode(toAst(ctx, Collections.emptyList()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private ValDeclExpressionNode toAst(OguParser.Val_defContext ctx) {
        ValDeclExpressionNode expr = new ValDeclExpressionNode(toAst(ctx, Collections.emptyList()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private FuncDeclExpressionNode toAst(OguParser.Func_defContext ctx) {
        FuncDeclExpressionNode expr = new FuncDeclExpressionNode(toAst(ctx, Collections.emptyList()));
        getPositionFrom(expr, ctx);
        return expr;
    }

    private ListExpressionNode toAst(OguParser.Vector_exprContext ctx) {
        if (ctx.list_expr() == null) {
            EmptyListExpressionNode empty = new EmptyListExpressionNode();
            getPositionFrom(empty, ctx);
            return empty;
        }
        return toAst(ctx.list_expr());
    }

    private ListExpressionNode toAst(OguParser.List_exprContext ctx) {
        if (ctx.e == null) {
            List<RangeExpressionNode> ranges = new ArrayList<>();
            for (OguParser.Range_exprContext r : ctx.le) {
                ranges.add(toAst(r));
            }
            ListByExtensionExpressionNode lexpr = new ListByExtensionExpressionNode(ranges);
            getPositionFrom(lexpr, ctx);
            return lexpr;
        } else {
            ExpressionNode value = toAst(ctx.e);
            List<SetConstraintNode> constraints = new ArrayList<>();
            for (OguParser.Set_constraint_exprContext sc:ctx.se) {
                constraints.add(toAst(sc));
            }
            ListByComprehensionExpressionNode list = new ListByComprehensionExpressionNode(value, constraints);
            getPositionFrom(list, ctx);
            return list;
        }
    }

    private SetConstraintNode toAst(OguParser.Set_constraint_exprContext ctx) {
        if (ctx.s_id != null) {
            SetConstraintNode cons = new SetConstraintNode(IdentifierNode.create(idText(ctx.s_id)), toAst(ctx.expr()));
            getPositionFrom(cons, ctx);
            return cons;
        } else {
            List<IdentifierNode> ids = ctx.l_id.stream().map((t) -> new IdentifierNode(idText(t))).collect(Collectors.toList());
            SetConstraintNode cons = new SetConstraintNode(ids, toAst(ctx.expr()));
            getPositionFrom(cons, ctx);
            return cons;
        }
    }

    private RangeExpressionNode toAst(OguParser.Range_exprContext ctx) {
        if (ctx.end == null) {
            InfiniteRangeExpressionNode range = new InfiniteRangeExpressionNode(toAst(ctx.beg));
            getPositionFrom(range, ctx);
            return range;
        }
        RangeExpressionNode range = new RangeExpressionNode(toAst(ctx.beg), toAst(ctx.end));
        getPositionFrom(range, ctx);
        return range;
    }

    private CaseExpressionNode toAst(OguParser.Case_exprContext ctx) {
        ExpressionNode selector = toAst(ctx.s);
        List<CaseGuard> guards = new ArrayList<>();
        for (OguParser.Case_guardContext cgctx : ctx.g.case_guard()) {
            ExpressionNode cond = toAst(cgctx.c);
            ExpressionNode result = toAst(cgctx.r);
            CaseGuard guard = new CaseGuard(cond, result);
            getPositionFrom(guard, ctx);
            guards.add(guard);
        }
        CaseExpressionNode caseExpr = new CaseExpressionNode(selector, guards);
        getPositionFrom(caseExpr, ctx);
        return caseExpr;
    }

    private IfExpressionNode toAst(OguParser.If_exprContext ctx) {
        ExpressionNode cond = toAst(ctx.cond);
        List<ExpressionNode> rest = toAst(ctx.then_part());
        IfExpressionNode ifExpr = new IfExpressionNode(cond, rest.get(0), rest.get(1));
        getPositionFrom(ifExpr, ctx);
        return ifExpr;
    }

    private void iterLetDecls(List<OguParser.Let_declContext> decls, List<ExpressionNode> exprs) {
        for (OguParser.Let_declContext decl : decls) {
            if (decl.expr() != null)
                exprs.add(toAst(decl.expr()));
            else if (decl.func_def() != null)
                exprs.add(toAst(decl.func_def()));
            else if (decl.val_def() != null)
                exprs.add(toAst(decl.val_def()));
            else {  ///var
                exprs.add(toAst(decl.var()));
            }
        }
    }

    private List<ExpressionNode> toAst(OguParser.Then_partContext ctx) {
        List<ExpressionNode> ifElems = new ArrayList<>();
        if (ctx.tb != null) {
            List<ExpressionNode> exprs = new ArrayList<>();
            iterLetDecls(ctx.tb.let_decl(), exprs);
            DoExpressionNode doExpr = new DoExpressionNode(exprs);
            getPositionFrom(doExpr, ctx);
            ifElems.add(doExpr);
        }
        else { // tb == null
            ifElems.add(toAst(ctx.te));
        }
        ifElems.add(toAst(ctx.else_part()));
        return ifElems;
    }

    private ExpressionNode toAst(OguParser.Else_partContext ctx) {
        if (ctx.eb == null) {
            return toAst(ctx.e);
        }
        else {
            List<ExpressionNode> exprs = new ArrayList<>();
            iterLetDecls(ctx.eb.let_decl(), exprs);
            DoExpressionNode doExpr = new DoExpressionNode(exprs);
            getPositionFrom(doExpr, ctx);
            return doExpr;
        }
    }

    private ConstructorNode toAst(OguParser.ConstructorContext ctx) {
        TypeReferenceNode type = new TypeReferenceNode(toAst(ctx.tid()));
        getPositionFrom(type, ctx);
        List<ActualParam> params;
        if (ctx.tuple_expr() == null)
            params = Collections.emptyList();
        else
            params = ctx.tuple_expr().expr().stream().map(this::toAstParam).collect(Collectors.toList());
        ConstructorNode ctor = new ConstructorNode(type, params);
        getPositionFrom(ctor, ctx);
        return ctor;
    }

    private ActualParam toAstParam(OguParser.ExprContext ctx) {
        ActualParam param = new ActualParam(toAst(ctx));
        getPositionFrom(param, ctx);
        return param;
    }

    private ExpressionNode toAst(OguParser.Paren_exprContext ctx) {
        if (ctx.tuple_expr() != null) {
            return toAst(ctx.tuple_expr());
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private ExpressionNode toAst(OguParser.Tuple_exprContext ctx) {
        if (ctx.e.size() == 1)
            return toAst(ctx.e.get(0));
        List<ExpressionNode> exprs = new ArrayList<>();
        for (OguParser.ExprContext ec : ctx.e) {
            exprs.add(toAst(ec));
        }
        TupleExpressionNode tuple = new TupleExpressionNode(exprs);
        getPositionFrom(tuple, ctx);
        return tuple;
    }

    private ExpressionNode toAst(OguParser.PrimaryContext ctx) {
        if (ctx.atom() != null) {
            return toAst(ctx.atom());
        }
        return toAst(ctx.neg_expr());
    }

    private NegExpressionNode toAst(OguParser.Neg_exprContext ctx) {
        NegExpressionNode ne;
        if (ctx.e != null)
            ne = new NegExpressionNode(toAst(ctx.e));
        else {
            if (ctx.a.d != null || ctx.a.string_literal != null)
                return new NegExpressionNodeError(message("error.expr.neg"), getPosition(ctx));
            ne = new NegExpressionNode(toAst(ctx.a));
        }
        getPositionFrom(ne, ctx);
        return ne;
    }

    private ExpressionNode toAst(OguParser.Func_nameContext ctx) {
        ReferenceNode id = new ReferenceNode(new IdentifierNode(idText(ctx.name)));
        getPositionFrom(id, ctx);
        return id;
    }

    private ExpressionNode toAst(OguParser.Qual_func_nameContext ctx) {
        if (ctx.name == null) {
            TypeIdentifierNode tname = TypeIdentifierNode.create(ctx.qual.stream().map(Token::getText).collect(Collectors.toList()));
            TypeReferenceNode ref = new TypeReferenceNode(tname);
            getPositionFrom(ref, ctx);
            return ref;
        } else {
            IdentifierNode tname = IdentifierNode.create(ctx.qual.stream().map(Token::getText).collect(Collectors.toList()), idText(ctx.name));
            ReferenceNode id = new ReferenceNode(tname);
            getPositionFrom(id, ctx);
            return id;
        }
    }


    private ExpressionNode toAst(OguParser.AtomContext ctx) {
        if (ctx.string_literal != null) {
            StringLiteral lit = new StringLiteral(idText(ctx.STRING().getSymbol()));
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.INT() != null) {
            String itxt = ctx.INT().getText().replace("_", "");
            BigInteger bi = new BigInteger(itxt);
            IntLiteral lit = new IntLiteral(bi);
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.CHAR() != null) {
            String ctxt = ctx.CHAR().getText();
            CharLiteral cl = new CharLiteral(ctxt);
            getPositionFrom(cl, ctx);
            return cl;
        }

        if (ctx.FLOAT() != null) {
            String dtxt = ctx.FLOAT().getText().replace("_", "");
            BigDecimal bd = new BigDecimal(dtxt);
            FloatLiteral lit = new FloatLiteral(bd);
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.DATE() != null) {
            String dtxt = ctx.DATE().getText();
            DateLiteral lit = new DateLiteral(dtxt);
            getPositionFrom(lit, ctx);
            return lit;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionCallNode toAstFunctionCall(OguParser.ExprContext ctx) {
        if (ctx.function != null) {
            ExpressionNode function = toAst(ctx.function);
            FunctionCallNode functionCallNode = new FunctionCallNode(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
            getPositionFrom(functionCallNode, ctx);
            return functionCallNode;

        }
        if (ctx.qual_function != null) {
            ExpressionNode function = toAst(ctx.qual_function);
            FunctionCallNode functionCallNode = new FunctionCallNode(function, ctx.expr().stream().map(this::toAstParam).collect(Collectors.toList()));
            getPositionFrom(functionCallNode, ctx);
            return functionCallNode;
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
