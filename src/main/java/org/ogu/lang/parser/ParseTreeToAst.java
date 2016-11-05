package org.ogu.lang.parser;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.ogu.lang.antlr.OguParser;
import org.ogu.lang.parser.ast.*;
import org.ogu.lang.parser.ast.decls.*;
import org.ogu.lang.parser.ast.decls.funcdef.*;
import org.ogu.lang.parser.ast.decls.typedef.ClassParamNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamConstrainedNode;
import org.ogu.lang.parser.ast.expressions.*;
import org.ogu.lang.parser.ast.expressions.control.*;
import org.ogu.lang.parser.ast.expressions.literals.*;
import org.ogu.lang.parser.ast.modules.*;
import org.ogu.lang.parser.ast.typeusage.*;
import org.ogu.lang.util.Logger;

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

    public ModuleNode toAst(File file, org.ogu.lang.antlr.OguParser.ModuleContext ctx) {
        ModuleNode module = new ModuleNode();
        getPositionFrom(module, ctx);
        module.setName(toAst(file, ctx.moduleHeader));

        OguParser.Module_bodyContext bodyCtx = ctx.module_body();
        for (OguParser.Module_declContext memberCtx : bodyCtx.module_decl()) {
            Node memberNode = toAst(memberCtx);
            if (memberNode instanceof ExpressionNode)
                module.add((ExpressionNode) memberNode);
            else if (memberNode instanceof AliasDeclarationNode)
                module.add((AliasDeclarationNode) memberNode);
            else if (memberNode instanceof ExportableDeclarationNode)
                module.add((ExportableDeclarationNode) memberNode);
            else {
                Logger.debug("WTF!");
            }
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

    private ExportsDeclarationNode toAst(OguParser.Export_nameContext ctx, List<DecoratorNode> decs) {
        ExportsDeclarationNode result;
        if (ctx.ID() != null)
            result = new ExportsFunctionDeclarationNode(new IdentifierNode(idText(ctx.ID().getSymbol())), decs);
        else if (ctx.TID() != null)
            result = new ExportsTypeDeclarationNode(TypeIdentifierNode.create(idText(ctx.TID().getSymbol())), decs);
        else {
            throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
        }
        getPositionFrom(result, ctx);
        return result;
    }

    private Node toAst(OguParser.Module_declContext ctx) {
        List<DecoratorNode> decs = toAstDecorators(ctx.decs);

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

    private InstanceDeclarationNode toAst(OguParser.Instance_defContext ctx, List<DecoratorNode> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeUsageWrapperNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParamNode> params = getInstaceTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclarationNode> members = internalDeclToAst(ctx.internal_decl());
        InstanceDeclarationNode instance = new InstanceDeclarationNode(name, params, members, decs);
        getPositionFrom(instance, ctx);
        return instance;
    }

    private List<TypeParamNode> getInstaceTypeParamList(OguParser.Instance_defContext ctx, List<OguParser.TypeContext> types, Map<String, TypeUsageWrapperNode> constraints) {
        List<TypeParamNode> params = new ArrayList<>();
        for (OguParser.TypeContext typeContext : types) {
            TypeUsageWrapperNode type = toAst(typeContext);
            TypeParamNode p = determineConstraint(type, constraints);
            getPositionFrom(p, ctx);
            params.add(p);
        }
        return params;

    }

    private TypeParamNode determineConstraint(TypeUsageWrapperNode type, Map<String, TypeUsageWrapperNode> constraints) {
        if (type instanceof IdTypeArgUsageNode) {
            if (constraints.containsKey(type.getName())) {
                TypeUsageWrapperNode tnode = constraints.get(type.getName());
                return new TypeParamConstrainedNode(type.getName(), tnode);
            }
        }
        if (type instanceof TupleTypeUsageNode) {
            TupleTypeUsageNode tt = (TupleTypeUsageNode) type;
            if (tt.getBases().size() == 1 && tt.getBase(0) instanceof GenericTypeUsageNode) {
                GenericTypeUsageNode gt = (GenericTypeUsageNode) tt.getBase(0);
                for (int i = 0; i < gt.getArgs().size(); i++) {
                    TypeUsageWrapperNode t = gt.getArg(i);
                    if (constraints.containsKey(t.getName())) {
                        TypeUsageWrapperNode ct = constraints.get(t.getName());
                        gt.setArg(i, new ConstrainedTypeUsageNode(new IdentifierNode(t.getName()), ct));
                    }
                }

            }
        }
        return new TypeParamNode(type);
    }

    private ClassDeclarationNode toAst(OguParser.Class_defContext ctx, List<DecoratorNode> decs) {
        boolean isMutable = ctx.mut != null;
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeUsageWrapperNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParamNode> genParams = getTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclarationNode> members = internalDeclToAst(ctx.internal_decl());
        List<ClassParamNode> params = new ArrayList<>();
        if (ctx.class_params() != null)
            params = toAst(ctx.class_params());
        ClassDeclarationNode clazz = new ClassDeclarationNode(name, isMutable, genParams, params, members, decs);
        getPositionFrom(clazz, ctx);
        return clazz;
    }

    private List<ClassParamNode> toAst(OguParser.Class_paramsContext ctx) {
        List<ClassParamNode> result = new ArrayList<>();
        for (OguParser.Class_paramContext cp:ctx.class_param()) {
            result.addAll(toAst(cp));
        }
        return result;
    }

    private List<ClassParamNode> toAst(OguParser.Class_paramContext ctx) {
        TypeUsageWrapperNode type = toAst(ctx.type());
        List<ClassParamNode> result = new ArrayList<>();
        for (TerminalNode cid:ctx.ID()) {
            IdentifierNode id = IdentifierNode.create(cid.getText());
            result.add(new ClassParamNode(id, type));
        }
        return result;
    }


    private EnumDeclarationNode toAst(OguParser.Enum_defContext ctx, List<DecoratorNode> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(ctx.en.getText());
        List<IdentifierNode> values = ctx.values.stream().map((t) -> new IdentifierNode(idText(t))).collect(Collectors.toList());
        List<TypeIdentifierNode> deriving = toDerivingAst(ctx.deriving());
        EnumDeclarationNode decl = new EnumDeclarationNode(name, values, deriving, decs);
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

    private DataDeclarationNode toAst(OguParser.Data_defContext ctx, List<DecoratorNode> decs) {
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String, TypeUsageWrapperNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);

        List<TypeParamNode> params;
        if (ctx.typedef_params() != null)
            params = getTypeParamList(ctx, ctx.typedef_params().params, constraints);
        else
            params = Collections.emptyList();
        List<TypeIdentifierNode> deriving = new ArrayList<>();
        List<TypeUsageWrapperNode> values = toAst(ctx.data_type_decl(), deriving);
        DataDeclarationNode decl = new DataDeclarationNode(name, params, values, deriving, decs);
        getPositionFrom(decl, ctx);
        return decl;
    }

    private List<TypeUsageWrapperNode> toAst(OguParser.Data_type_declContext ctx, List<TypeIdentifierNode> deriving) {
        if (ctx.deriving() != null)
            deriving.addAll(toDerivingAst(ctx.deriving()));
        return ctx.t.stream().map(this::toAst).collect(Collectors.toList());
    }

    private TraitDeclarationNode toAst(OguParser.Trait_defContext ctx, List<DecoratorNode> decs) {
        boolean isMutable = ctx.mut != null;
        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.name));
        Map<String,TypeUsageWrapperNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);
        List<TypeParamNode> params = getTypeParamList(ctx, ctx.params, constraints);
        List<FunctionalDeclarationNode> members = internalDeclToAst(ctx.internal_decl());
        TraitDeclarationNode trait = new TraitDeclarationNode(name, isMutable, params, members, decs);
        getPositionFrom(trait, ctx);
        return trait;
    }

    private List<FunctionalDeclarationNode> internalDeclToAst(List<OguParser.Internal_declContext> decls) {
        return decls.stream().map(this::toAst).collect(Collectors.toList());
    }

    private FunctionalDeclarationNode toAst(OguParser.Internal_declContext ctx) {
        List<DecoratorNode> decs = toAstDecorators(ctx.decorators());

        if (ctx.func_decl() != null)
            return toAst(ctx.func_decl(), decs);
        if (ctx.val_def() != null)
            return toAst(ctx.val_def(), decs);
        if (ctx.func_def() != null)
            return toAst(ctx.func_def(), decs);
        if (ctx.var() != null)
            return toAst(ctx.var(), decs);
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private TypedefDeclarationNode toAst(OguParser.Type_defContext ctx, List<DecoratorNode> decs) {
        Map<String,TypeUsageWrapperNode> constraints = new HashMap<>();
        if (ctx.constraints != null)
            loadTypeConstraints(ctx.constraints, constraints);


        TypeIdentifierNode name = new TypeIdentifierNode(idText(ctx.t));
        TypeUsageWrapperNode type = toAst(ctx.type());
        getPositionFrom(name, ctx);
        if (ctx.ta == null)  {
            SimpleTypeDeclarationNode tdecl = new SimpleTypeDeclarationNode(name, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        } else {
            // TODO Warning if are unused contraints
            List<TypeParamNode> params = getTypeParamList(ctx, ctx.typedef_params().params, constraints);
            GenericTypeDeclarationNode tdecl = new GenericTypeDeclarationNode(name, params, type, decs);
            getPositionFrom(tdecl, ctx);
            return tdecl;
        }
    }

    private List<TypeParamNode> getTypeParamList(ParserRuleContext ctx, List<Token> tokens, Map<String,TypeUsageWrapperNode> constraints) {
        if (ctx == null)
            return Collections.emptyList();

        List<TypeParamNode> params = new ArrayList<>();
        for (Token token : tokens) {
            String id = idText(token);
            TypeParamNode p;
            if (constraints.containsKey(id))
                p = new TypeParamConstrainedNode(id, constraints.get(id));
            else
                p = new TypeParamNode(id);
            getPositionFrom(p, ctx);
            params.add(p);
        }
        return params;
    }

    private void loadTypeConstraints(OguParser.Typedef_args_constraintsContext ctx, Map<String, TypeUsageWrapperNode> cons) {
        for (OguParser.Typedef_arg_constraintContext tac:ctx.tac) {
            TypeUsageWrapperNode type = toAst(tac.type());
            for (Token id : tac.ids) {
                cons.put(idText(id), type);
            }
        }
    }

    private FunctionalDeclarationNode toAst(OguParser.Func_defContext ctx, List<DecoratorNode> decoratorNodes) {
        if (ctx.let_func_name != null) {
            if (ctx.let_func_name.lid_val_id != null) {
                // let is really a val
                IdentifierNode funcId = IdentifierNode.create(idText(ctx.let_func_name.lid_val_id));
                TypeUsageWrapperNode type = toAst(ctx.let_func_name.t);
                if (ctx.let_func_args != null && !ctx.let_func_args.isEmpty()) {
                    return new ErrorFunctionalDeclarationNode("error.let_as_val.no_params", getPosition(ctx));
                }
                Logger.debug("let is val: "+ctx.getText());
                ExpressionNode expr = toAst(ctx.let_expr());
                ValDeclarationNode val = new ValDeclarationNode(funcId, type, expr, decoratorNodes);
                getPositionFrom(val, ctx);
                return val;
            }
            if (ctx.let_func_name.lid_fun_id != null) {
                IdentifierNode funcId = IdentifierNode.create(idText(ctx.let_func_name.lid_fun_id));
                List<FunctionPatternParamNode> params = funcArgsToAst(ctx.let_func_args);
                LetDefinitionNode funcdef = new LetDefinitionNode(funcId, params, decoratorNodes);
                getPositionFrom(funcdef, ctx);
                if (ctx.let_expr() != null) {
                    toAst(ctx.let_expr(), funcdef);

                }
                return funcdef;
            }
        }
        if (ctx.infix_op != null || ctx.prefix_op != null) {
            List<FunctionPatternParamNode> params = new ArrayList<>();
            params.add(toAst(ctx.left));
            params.add(toAst(ctx.right));
            OperatorNode op = ctx.infix_op != null ? toAst(ctx.infix_op) : toAst(ctx.prefix_op);
            OpDefinitionNode opDef = new OpDefinitionNode(op, params, decoratorNodes);
            getPositionFrom(opDef, ctx);
            if (ctx.let_expr() != null) {
                toAst(ctx.let_expr(), opDef);
            }
            return opDef;
        }

        if (!ctx.tup.isEmpty()) {
            List<IdentifierNode> ids = new ArrayList<>();
            Map<IdentifierNode, TypeUsageWrapperNode> types = new HashMap<>();
            ErrorValDeclarationNode error = lidsToAst(ctx.tup, ids, types);
            if (error != null)
                return error;
            ExpressionNode value = toAst(ctx.expr());
            TupleValDeclarationNode decl = new TupleValDeclarationNode(ids, types, value, decoratorNodes);
            getPositionFrom(decl, ctx);
            return decl;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private ErrorValDeclarationNode lidsToAst(List<OguParser.LidContext> lids, List<IdentifierNode> ids, Map<IdentifierNode, TypeUsageWrapperNode> types) {
        for(OguParser.LidContext ctx : lids) {
            IdentifierNode id;
            if (ctx.lid_fun_id !=null) {
                id = IdentifierNode.create(idText(ctx.lid_fun_id));
            } else {
                id = IdentifierNode.create(idText(ctx.lid_val_id));
            }

            if (ids.contains(id)) {
                return new ErrorValDeclarationNode(message("error.declare_duplicate_id") + id.getName(), getPosition(ctx));
            }
            ids.add(id);
            if (ctx.type() != null) {
                TypeUsageWrapperNode type = toAst(ctx.type());
                types.put(id, type);
            }
        }
        return null;
    }

    private ErrorVarDeclarationNode vidtToAst(List<OguParser.VidtContext> vidt, List<IdentifierNode> ids, Map<IdentifierNode, TypeUsageWrapperNode> types) {
        for (OguParser.VidtContext ctx : vidt) {
            IdentifierNode id = IdentifierNode.create(idText(ctx.i));
            if (ids.contains(id))
                return new ErrorVarDeclarationNode(message("error.declare_duplicate_id") + id.getName(), getPosition(ctx));
            ids.add(id);
            if (ctx.type() != null) {
                TypeUsageWrapperNode type = toAst(ctx.type());
                types.put(id, type);
            }
        }
        return null;
    }

    private ExpressionNode toAst(OguParser.Let_exprContext ctx) {
        if (ctx.expr() != null) {
            return toAst(ctx.expr());
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private void toAst(OguParser.Let_exprContext ctx, LetDeclarationNode funcdef) {
        if (ctx.let_block() != null) {
            toAst(ctx.let_block(), funcdef);
            if (ctx.let_block().where() != null)
                parseWhere(ctx.let_block().where(), funcdef);
            return;
        }
        if (ctx.expr() != null) {
            funcdef.add(toAst(ctx.expr()));
            if (ctx.let_where() != null)
                parseWhere(ctx.let_where().where(), funcdef);
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

    private void parseWhere(OguParser.WhereContext ctx, LetDeclarationNode funcdef) {
        for (OguParser.Where_exprContext wc:ctx.wl) {
            parseWhereCtx(wc, funcdef);
        }
    }

    private void parseWhereCtx(OguParser.Where_exprContext ctx, LetDeclarationNode funcdef) {
        if (ctx.i != null) {
            IdentifierNode funcId = IdentifierNode.create(idText(ctx.i));
            List<FunctionPatternParamNode> params = funcArgsToAst(ctx.let_arg());
            LetDefinitionNode letDef = new LetDefinitionNode(funcId, params, Collections.emptyList());
            getPositionFrom(letDef, ctx);
            if (ctx.let_expr() != null) {
                toAst(ctx.let_expr(), letDef);
            }
            WhereDeclarationNode where = new WhereDeclarationNode(letDef);
            getPositionFrom(where, ctx);
            funcdef.add(where);
            return;
        }
        else {
            if (!ctx.tup.isEmpty()) {
                List<IdentifierNode> ids = new ArrayList<>();
                Map<IdentifierNode, TypeUsageWrapperNode> types = new HashMap<>();
                lidsToAst(ctx.tup, ids, types);
                ExpressionNode value = toAst(ctx.expr());
                TupleValDeclarationNode decl = new TupleValDeclarationNode(ids, types, value, Collections.emptyList());
                getPositionFrom(decl, ctx);
                WhereDeclarationNode where = new WhereDeclarationNode(decl);
                getPositionFrom(where, ctx);
                funcdef.add(where);
                return;
            }

        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private GuardDeclarationNode toAst(OguParser.GuardContext ctx) {
        ExpressionNode base = toAst(ctx.be);
        List<ExpressionNode> args = ctx.ae.stream().map(this::toAst).collect(Collectors.toList());
        List<ExpressionNode> exprs = new ArrayList<>();
        if (ctx.de != null)
            exprs.add(toAst(ctx.de));
        else {
            iterLetDecls(ctx.eb.let_decl(), exprs);
        }
        DoExpressionNode doExpr = new DoExpressionNode(exprs);
        getPositionFrom(doExpr, ctx);
        GuardDeclarationNode guard = new GuardDeclarationNode(base, args, doExpr);
        getPositionFrom(guard, ctx);
        return guard;
    }

    private void toAst(OguParser.Let_blockContext ctx, LetDeclarationNode funcdef) {
        for (OguParser.Let_declContext decl : ctx.ld) {
            if (decl.expr() != null)
                funcdef.add(toAst(decl.expr()));
            else if (decl.func_def() != null)
                funcdef.add(new FunctionDeclNode(toAst(decl.func_def(),Collections.emptyList())));
            else { ///var
                funcdef.add(new FunctionDeclNode(toAst(decl.var(), Collections.emptyList())));
            }
        }
    }


    private List<FunctionPatternParamNode> funcArgsToAst(List<OguParser.Let_argContext> let_func_args) {
        return let_func_args.stream().map(this::toAst).collect(Collectors.toList());
    }

    private FunctionPatternParamNode toAst(OguParser.Let_argContext ctx) {


        if (ctx.l_atom != null)
            return toAst(ctx.l_atom);

        if (ctx.let_arg_vector() != null)
            return toAst(ctx.let_arg_vector());


        if (ctx.let_arg_tuple_or_list() != null)
            return toAst(ctx.let_arg_tuple_or_list());

        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionPatternParamNode toAst(OguParser.Let_arg_tuple_or_listContext ctx) {
        if (ctx.la != null && !ctx.la.isEmpty()) {
            List<FunctionPatternParamNode> args = ctx.la.stream().map(this::toAst).collect(Collectors.toList());
            FuncListParamNode param = new FuncListParamNode(args);
            getPositionFrom(param, ctx);
            return param;
        }
        if (ctx.ta != null && !ctx.ta.isEmpty()) {
            List<FunctionPatternParamNode> args = ctx.ta.stream().map(this::toAst).collect(Collectors.toList());
            FuncTupleParamNode param = new FuncTupleParamNode(args);
            getPositionFrom(param, ctx);
            return param;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionPatternParamNode toAst(OguParser.Let_arg_vectorContext ctx) {
        if (ctx.la == null || ctx.la.isEmpty()) {
            FuncEmptyVectorParamNode param = new FuncEmptyVectorParamNode();
            getPositionFrom(param, ctx);
            return param;
        }
        List<FunctionPatternParamNode> args = ctx.la.stream().map(this::toAst).collect(Collectors.toList());
        FuncVectorParamNode param = new FuncVectorParamNode(args);
        getPositionFrom(param, ctx);
        return param;
    }

    private FunctionPatternParamNode toAst(OguParser.Let_arg_atomContext ctx) {
        if (ctx.l_id != null) {
            if (ctx.l_id.lid_fun_id != null) {
                FuncIdParamNode id = toAst(ctx.l_id.lid_fun_id);
                getPositionFrom(id, ctx);
                return id;
            }
            else {
                TypeUsageWrapperNode type = toAst(ctx.l_id.type());
                IdentifierNode id = IdentifierNode.create(idText(ctx.l_id.lid_val_id));
                FuncIdTypeParamNode param = new FuncIdTypeParamNode(id, type);
                getPositionFrom(param, ctx);
                return param;
            }
        }
        if (ctx.t_id != null)
        {
            TypeIdentifierNode tid =  TypeIdentifierNode.create(idText(ctx.t_id));
            if (ctx.la == null) {
                FuncTypeParamNode typeParam = new FuncTypeParamNode(tid);
                getPositionFrom(typeParam, ctx);
                return typeParam;
            } else {
                List<FunctionPatternParamNode> args = ctx.la.stream().map(this::toAst).collect(Collectors.toList());
                FuncGenericTypeParamNode typeParam = new FuncGenericTypeParamNode(tid, args);
                getPositionFrom(typeParam, ctx);
                return typeParam;
            }
        }

        if (ctx.a != null) {
            FuncExprParamNode param = new FuncExprParamNode(toAst(ctx.atom()));
            getPositionFrom(param, ctx);
            return param;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FuncIdParamNode toAst(Token tok) {
        return new FuncIdParamNode(IdentifierNode.create(idText(tok)));
    }

    private List<DecoratorNode> toAstDecorators(OguParser.DecoratorsContext decs) {
        if (decs == null)
            return Collections.emptyList();
        return decs.dec.stream().map(this::toAst).collect(Collectors.toList());
    }

    private DecoratorNode toAst(OguParser.DecoratorContext ctx) {
        String decoratorId = idText(ctx.dec_id);
        if ("extern".equals(decoratorId)) {
            List<String> decoratorArgs = ctx.dec_args.stream().map(this::idText).collect(Collectors.toList());
            if (decoratorArgs.size() != 2)
                return new ErrorDecoratorNode(message("error.decorator.wrong_size_of_arguments"), getPosition(ctx));
            DecoratorNode decoratorNode = new ExternDecoratorNode(decoratorArgs.get(0), decoratorArgs.get(1));
            getPositionFrom(decoratorNode, ctx);
            return decoratorNode;
        }
        if ("primitive".equals(decoratorId)) {
            DecoratorNode decoratorNode = new PrimitiveDecoratorNode();
            getPositionFrom(decoratorNode, ctx);
            return decoratorNode;
        }
        if ("entrypoint".equals(decoratorId)) {
            DecoratorNode decoratorNode = new EntryPointDecoratorNode();
            getPositionFrom(decoratorNode, ctx);
            return decoratorNode;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionalDeclarationNode toAst(OguParser.Func_declContext ctx, List<DecoratorNode> decoratorNodes) {
        if (ctx.name.f_id != null) {
            List<TypeArgUsageWrapperNode> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            FunctionDeclarationNode funcDecl = new FunctionDeclarationNode(toAst(ctx.name), params, decoratorNodes);
            getPositionFrom(funcDecl, ctx);
            return funcDecl;
        }
        if (ctx.name.f_op != null) {
            // op params...
            List<TypeArgUsageWrapperNode> params = ctx.func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            OpDeclarationNode opDecl = new OpDeclarationNode(toAst(ctx.name.f_op), params, decoratorNodes);
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

    private TypeArgUsageWrapperNode toAst(OguParser.Func_decl_argContext ctx) {
        if (ctx.unit() != null)
            return new UnitTypeArgUsageNode();
        if (ctx.fda_id != null)
            return new QualifiedTypeArgUsageNode(TypeIdentifierNode.create(idText(ctx.fda_id)));
        if (!ctx.fda_tid.isEmpty()) {
            TypeIdentifierNode id = TypeIdentifierNode.create(ctx.fda_tid.stream().map(this::idText).collect(Collectors.toList()));
            if (ctx.tid_or_id_arg == null || ctx.tid_or_id_arg.isEmpty()){
                return new QualifiedTypeArgUsageNode(id);
            } else {
                List<NameNode> args = new ArrayList<>();
                for (OguParser.Tid_or_idContext ti:ctx.tid_or_id_arg)
                    if (ti.i != null)
                        args.add(IdentifierNode.create(idText(ti.i)));
                    else
                        args.add(TypeIdentifierNode.create(idText(ti.t)));
                GenericTypeArgUsageNode genType = new GenericTypeArgUsageNode(id, args);
                getPositionFrom(genType, ctx);
                return genType;
            }
        }
        if (ctx.vector() != null) {
            TypeArgUsageWrapperNode arg = toAst(ctx.vector().func_decl_arg());
            VectorTypeArgUsageNode vec = new VectorTypeArgUsageNode(arg);
            getPositionFrom(vec, ctx);
            return vec;
        }
        if (ctx.tuple() != null) {
            List<TypeArgUsageWrapperNode> args = ctx.tuple().func_decl_arg().stream().map(this::toAst).collect(Collectors.toList());
            TupleTypeArgUsageNode tuple = new TupleTypeArgUsageNode(args);
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

    private List<ExportsDeclarationNode> toAst(OguParser.Module_exportsContext ctx, List<DecoratorNode> decoratorNodes) {
        if (ctx.export_name() != null)
            return ctx.exports.stream().map((e) -> toAst(e, decoratorNodes)).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }


    private List<UsesDeclarationNode> toAst(OguParser.Module_usesContext ctx, List<DecoratorNode> decs) {
        if (ctx.module_name() != null)
            return ctx.imports.stream().map((i) -> new UsesDeclarationNode(toAst(i), decs)).collect(Collectors.toList());
        throw new UnsupportedOperationException(ctx.toString());
    }

    private AliasDeclarationNode toAst(OguParser.Alias_defContext ctx, List<DecoratorNode> decs) {
        OguParser.Alias_targetContext target = ctx.alias_target();
        OguParser.Alias_originContext origin = ctx.alias_origin();
        if (target.alias_tid != null) {

            if (origin.alias_origin_id != null) {
                return new ErrorAliasNode(message("error.alias.tid_no_tid"), getPosition(ctx));
            }

            // jvm interop
            if (origin.jvm_id != null) {
                AliasTypeJvmInteropDeclarationNode decl = new AliasTypeJvmInteropDeclarationNode(toOguTypeIdentifier(target), origin.jvm_id.STRING().getText(), decs);
                getPositionFrom(decl, ctx);
                return decl;
            }

            TypeAliasDeclarationNode decl = new TypeAliasDeclarationNode(toOguTypeIdentifier(target), toOguTypeIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        } else {
            if (origin.jvm_id != null) {
                String src = origin.jvm_origin().src.getText().replaceAll("\"z", "");
                AliasJvmInteropDeclarationNode decl = new AliasJvmInteropDeclarationNode(toOguIdentifier(target), decs, src);
                return decl;
            }
            if (origin.alias_origin_id == null) {
                return new ErrorAliasNode(message("error.alias.id_no_id"), getPosition(ctx));
            }
            IdAliasDeclarationNode decl = new IdAliasDeclarationNode(toOguIdentifier(target), toOguIdentifier(origin), decs);
            getPositionFrom(decl, ctx);
            return decl;
        }
    }


    private VarDeclarationNode toAst(OguParser.VarContext ctx, List<DecoratorNode> decs) {
        if (ctx.vid != null) {
            IdentifierNode id =  IdentifierNode.create(idText(ctx.vid));
            VarDeclarationNode var;
            if (ctx.type() == null) {
                var = new VarDeclarationNode(id, toAst(ctx.expr()), decs);
            } else {
                if (ctx.expr() != null)
                    var = new VarDeclarationNode(id, toAst(ctx.type()), toAst(ctx.expr()), decs);
                else
                    var = new VarDeclarationNode(id, toAst(ctx.type()), decs);
            }
            getPositionFrom(var, ctx);
            return var;
        } else {
            List<IdentifierNode> ids = new ArrayList<>();
            Map<IdentifierNode, TypeUsageWrapperNode> types = new HashMap<>();
            ErrorVarDeclarationNode error = vidtToAst(ctx.vidt(), ids, types);
            if (error != null)
                return error;
            ExpressionNode value = toAst(ctx.expr());
            TupleVarDeclarationNode decl = new TupleVarDeclarationNode(ids, types, value, decs);
            getPositionFrom(decl, ctx);
            return decl;
        }
    }

    private ValDeclarationNode toAst(OguParser.Val_defContext ctx, List<DecoratorNode> decs) {
        if (ctx.val_id != null) {
            if (ctx.type() == null) {
                ValDeclarationNode val = new ValDeclarationNode(IdentifierNode.create(idText(ctx.val_id)), toAst(ctx.expr()), decs);
                getPositionFrom(val, ctx);
                return val;
            }
            ValDeclarationNode val = new ValDeclarationNode(IdentifierNode.create(idText(ctx.val_id)), toAst(ctx.type()), toAst(ctx.expr()), decs);
            getPositionFrom(val, ctx);
            return val;
        } else {
            List<IdentifierNode> ids = new ArrayList<>();
            Map<IdentifierNode, TypeUsageWrapperNode> types = new HashMap<>();
            ErrorValDeclarationNode error  = lidsToAst(ctx.lid(), ids, types);
            if (error != null) {
                return error;
            } else {
                ExpressionNode expr = toAst(ctx.expr());
                TupleValDeclarationNode vals = new TupleValDeclarationNode(ids, types, expr, decs);
                getPositionFrom(expr, ctx);
                return vals;
            }
        }
    }

    private TypeUsageWrapperNode toAst(OguParser.TypeContext ctx) {
        if (ctx.vector_type() != null) {
            TypeUsageWrapperNode type = toAst(ctx.vector_type().type());
            VectorTypeUsageNode vtype = new VectorTypeUsageNode(type);
            getPositionFrom(vtype, ctx);
            return vtype;
        }
        if (ctx.unit() != null) {
            UnitTypeUsageNode utype = new UnitTypeUsageNode();
            getPositionFrom(utype, ctx);
            return utype;
        }
        if (ctx.tuple_type() != null) {
            List<TypeUsageWrapperNode> types = ctx.tuple_type().type().stream().map(this::toAst).collect(Collectors.toList());
            TupleTypeUsageNode ttype = new TupleTypeUsageNode(types);
            getPositionFrom(ttype, ctx);
            return ttype;
        }
        if (ctx.map_type() != null) {
            TypeUsageWrapperNode key = toAst(ctx.map_type().k);
            TypeUsageWrapperNode val = toAst(ctx.map_type().v);
            MapTypeUsageNode type = new MapTypeUsageNode(key, val);
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
                return new QualifiedTypeArgUsageNode(toAst(ctx.tid()));
            } else {
                List<TypeUsageWrapperNode> args = ctx.t_a.stream().map(this::toAst).collect(Collectors.toList());
                TypeIdentifierNode tName = toAst(ctx.gt);
                GenericTypeUsageNode type = new GenericTypeUsageNode(tName, args);
                getPositionFrom(type, ctx);
                return type;
            }
        }
        if (ctx.nat != null) {
            NativeTypeUsageNode type = new NativeTypeUsageNode(idText(ctx.nat));
            getPositionFrom(type, ctx);
            return type;
        }
        if (ctx.i != null) {
            IdTypeArgUsageNode type = new IdTypeArgUsageNode(IdentifierNode.create(idText(ctx.i)));
            getPositionFrom(type, ctx);
            return type;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }



    private TypeUsageWrapperNode toAst(OguParser.Tid_argsContext ctx) {
        if (ctx.i != null) {
            IdTypeArgUsageNode type = new IdTypeArgUsageNode(IdentifierNode.create(idText(ctx.i)));
            getPositionFrom(type, ctx);
            return type;
        }
        if (ctx.type() != null) {
            return toAst(ctx.type());
        }
        if (ctx.tid() != null) {
            QualifiedTypeUsageNode type = new QualifiedTypeUsageNode(toAst(ctx.tid()));
            getPositionFrom(type, ctx);
            return type;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private RecordTypeUsageNode toAst(OguParser.Record_typeContext ctx) {
        List<RecordFieldNode> fields = ctx.fldDecl().stream().map(this::toAst).collect(Collectors.toList());
        TypeIdentifierNode name = TypeIdentifierNode.create(idText(ctx.ti));
        RecordTypeUsageNode record = new RecordTypeUsageNode(name, fields);
        getPositionFrom(record, ctx);
        return record;
    }

    private AnonRecordTypeUsageNode toAst(OguParser.Anon_record_typeContext ctx) {
        List<RecordFieldNode> fields = ctx.fldDecl().stream().map(this::toAst).collect(Collectors.toList());
        AnonRecordTypeUsageNode record = new AnonRecordTypeUsageNode(fields);
        getPositionFrom(record, ctx);
        return record;
    }

    private RecordFieldNode toAst(OguParser.FldDeclContext ctx) {
        IdentifierNode id = IdentifierNode.create(idText(ctx.i));
        TypeUsageWrapperNode type = toAst(ctx.t);
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


        if (ctx.infix_id != null) {
            ExpressionNode l = toAst(ctx.l_infix);
            ExpressionNode r = toAst(ctx.r_infix);
            List<ActualParamNode> params = new ArrayList<>();
            params.add(new ActualParamNode(l));
            params.add(new ActualParamNode(r));
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
            String op = ctx.o.getText();
            if (op.equals("*") || op.equals("/") || op.equals("//") || op.equals("+") || op.equals("-")) {
                MathOpExpressionNode expr = new MathOpExpressionNode(new OperatorNode(ctx.o.getText()), left, right);
                getPositionFrom(expr, ctx);
                return expr;
            }
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


    private ExpressionNode toAst(OguParser.Param_exprContext ctx) {

        if (ctx.self_id() != null) {
            return toAst(ctx.self_id());
        }

        if (ctx.ref != null) {
            ReferenceNode ref = new ReferenceNode(IdentifierNode.create(idText(ctx.ref)));
            getPositionFrom(ref, ctx);
            return ref;
        }
        if (ctx.primary() != null) {
            return toAst(ctx.primary());
        }

        if (ctx.paren_expr() != null) {
            return toAst(ctx.paren_expr());
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
        List<ExpressionNode> args = ctx.expr().stream().map(this::toAst).collect(Collectors.toList());
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
            List<RangeExpressionNode> ranges = ctx.le.stream().map(this::toAst).collect(Collectors.toList());
            ListByExtensionExpressionNode lexpr = new ListByExtensionExpressionNode(ranges);
            getPositionFrom(lexpr, ctx);
            return lexpr;
        } else {
            ExpressionNode value = toAst(ctx.e);
            List<SetConstraintNode> constraints = ctx.se.stream().map(this::toAst).collect(Collectors.toList());
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
        List<CaseGuardNode> guards = new ArrayList<>();
        for (OguParser.Case_guardContext cgctx : ctx.g.case_guard()) {
            ExpressionNode cond = toAst(cgctx.c);
            ExpressionNode result = toAst(cgctx.r);
            CaseGuardNode guard = new CaseGuardNode(cond, result);
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

    /*
    private ConstructorNode toAst(OguParser.ConstructorContext ctx) {
        TypeReferenceNode type = new TypeReferenceNode(toAst(ctx.tid()));
        getPositionFrom(type, ctx);
        List<ActualParamNode> params;
        if (ctx.tuple_expr() == null)
            params = Collections.emptyList();
        else
            params = ctx.tuple_expr().expr().stream().map(this::toAstParam).collect(Collectors.toList());
        ConstructorNode ctor = new ConstructorNode(type, params);
        getPositionFrom(ctor, ctx);
        return ctor;
    } */

    private ActualParamNode toAstParam(OguParser.Param_exprContext ctx) {
        ActualParamNode param = new ActualParamNode(toAst(ctx));
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
        List<ExpressionNode> exprs = ctx.e.stream().map(this::toAst).collect(Collectors.toList());
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
            StringLiteralNode lit = new StringLiteralNode(idText(ctx.STRING().getSymbol()));
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.INT() != null) {
            String itxt = ctx.INT().getText().replace("_", "");
            int bi = new Integer(itxt);
            IntLiteralNode lit = new IntLiteralNode(bi);
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.CHAR() != null) {
            String ctxt = ctx.CHAR().getText();
            CharLiteralNode cl = new CharLiteralNode(ctxt);
            getPositionFrom(cl, ctx);
            return cl;
        }

        if (ctx.FLOAT() != null) {
            String dtxt = ctx.FLOAT().getText().replace("_", "");
            double bd = new Double(dtxt);
            DoubleLiteralNode lit = new DoubleLiteralNode(bd);
            getPositionFrom(lit, ctx);
            return lit;
        }
        if (ctx.DATE() != null) {
            String dtxt = ctx.DATE().getText();
            DateLiteralNode lit = new DateLiteralNode(dtxt);
            getPositionFrom(lit, ctx);
            return lit;
        }
        Logger.debug(ctx.getText());
        throw new UnsupportedOperationException(ctx.getClass().getCanonicalName());
    }

    private FunctionCallNode toAstFunctionCall(OguParser.ExprContext ctx) {
        if (ctx.function != null) {
            ExpressionNode function = toAst(ctx.function);
            List<ActualParamNode> actualParams = new ArrayList<>();
            if (ctx.params_expr() != null)
                actualParams.addAll(ctx.params_expr().param_expr().stream().map(this::toAstParam).collect(Collectors.toList()));
            FunctionCallNode functionCallNode = new FunctionCallNode(function, actualParams);
            getPositionFrom(functionCallNode, ctx);
            return functionCallNode;

        }
        if (ctx.qual_function != null) {
            ExpressionNode function = toAst(ctx.qual_function);
            FunctionCallNode functionCallNode = new FunctionCallNode(function, ctx.params_expr().param_expr().stream().map(this::toAstParam).collect(Collectors.toList()));
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
