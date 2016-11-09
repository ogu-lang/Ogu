package org.ogu.lang.compiler;

import com.google.common.collect.ImmutableList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.codegen.bytecode_generation.returnop.ReturnVoidBS;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasJvmInteropDeclarationNode;
import org.ogu.lang.parser.ast.decls.LetDeclarationNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.parser.ast.typeusage.UnitTypeUsageNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.util.Feedback;
import org.ogu.lang.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;


/**
 * A Compilation phase
 * Created by ediaz on 21-01-16.
 */
public class Compilation {

    private static final int JAVA_8_CLASS_VERSION = 52;
    static final String OBJECT_INTERNAL_NAME = JvmNameUtils.canonicalToInternal(Object.class.getCanonicalName());
    private final static String METHOD_NAME_OF_FUNCTION = "invoke";

    private SymbolResolver resolver;
    private ErrorCollector errorCollector;
    private Options options;
    private ClassWriter cw;
    private final CompilationOfExpressions compilationOfExpressions = new CompilationOfExpressions(this);

    public Compilation(SymbolResolver resolver, ErrorCollector errorCollector, Options options) {
        this.resolver = resolver;
        this.errorCollector = errorCollector;
        this.options = options;
    }

    public List<ClassFileDefinition> compile(ModuleNode module) {
        boolean valid = module.validate(resolver, errorCollector);

        if (!valid) {
            return Collections.emptyList();
        }

        List<ClassFileDefinition> classFileDefinitions = new ArrayList<>();

        if (options.isShowTree()) {
            for (Node node : module.getChildren()) {
                Feedback.message("Node: "+node+" context: "+node.contextName());
            }
        }

        Map<String, List<LetDeclarationNode>> letDecls = new HashMap<>();
        for (Node node : module.getChildren()) {
            if (node instanceof LetDeclarationNode) {
                LetDeclarationNode let = (LetDeclarationNode) node;
                if (!letDecls.containsKey(let.getName())) {
                    letDecls.put(let.getName(), new ArrayList<>());
                }
                letDecls.get(let.getName()).add(let);
            }
        }

        // overloaded let decls
        for (String letName : letDecls.keySet()) {
            classFileDefinitions.addAll(compile(letName, letDecls.get(letName), module));
        }

        classFileDefinitions.add(compileProgram(module));
        return classFileDefinitions;
    }

    private ClassFileDefinition compile(AliasJvmInteropDeclarationNode node) {
        throw new RuntimeException("ESPERA");
    }

    private List<ClassFileDefinition> compile(String letName, List<LetDeclarationNode> letDeclarations, ModuleNode nameSpace) {
        String internalName = JvmNameUtils.renameFromOguToJvm(letName);
        String canonicalClassName = nameSpace.getNameDefinition().getName()
                + "." + LetDeclarationNode.CLASS_PREFIX + internalName;
        String internalClassName = JvmNameUtils.canonicalToInternal(canonicalClassName);

        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(JAVA_8_CLASS_VERSION, ACC_PUBLIC+ACC_SUPER, internalClassName, null, OBJECT_INTERNAL_NAME, null);

        for (LetDeclarationNode letDeclaration: letDeclarations) {
            generateInvocable(letDeclaration, METHOD_NAME_OF_FUNCTION, true);
        }
        return ImmutableList.of(endClass(canonicalClassName));
    }

    private ClassFileDefinition compileProgram(ModuleNode module) {
        if (localVarsSymbolTable == null) {
            localVarsSymbolTable = LocalVarsSymbolTable.forInstanceMethod();
        }

        String canonicalName = module.getNameDefinition().contextName();
        String internalName = JvmNameUtils.canonicalToInternal(canonicalName);

        Feedback.message("class File Definiton "+canonicalName+" ("+internalName+")");

        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        createConstructor(module);

        cw.visit(JAVA_8_CLASS_VERSION, ACC_PUBLIC + ACC_SUPER, internalName, null, OBJECT_INTERNAL_NAME, null);


        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();


        for (ExpressionNode expr : module.getProgram()) {
            compilationOfExpressions.compile(expr).operate(mv);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return endClass(canonicalName);
    }

    private ClassFileDefinition endClass(String canonicalName) {
        cw.visitEnd();

        byte[] programByteCode = cw.toByteArray();
        cw = null;
        return new ClassFileDefinition(canonicalName, programByteCode);
    }


    private void createConstructor(ModuleNode module) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    SymbolResolver getResolver() {
        return resolver;
    }

    private LocalVarsSymbolTable localVarsSymbolTable;

    LocalVarsSymbolTable getLocalVarsSymbolTable() {
        return localVarsSymbolTable;
    }

    public void setLocalVarsSymbolTable(LocalVarsSymbolTable localVarsSymbolTable) {
        this.localVarsSymbolTable = localVarsSymbolTable;
    }


    private void generateInvocable(LetDeclarationNode invocableDefinition, String invocableName, boolean isStatic) {
        if (isStatic) {
            localVarsSymbolTable = LocalVarsSymbolTable.forStaticMethod();
        } else {
            localVarsSymbolTable = LocalVarsSymbolTable.forInstanceMethod();
        }

        String paramsDescriptor = String.join("", invocableDefinition.getParameters().stream().map((dp) -> dp.getType().jvmType().getDescriptor()).collect(Collectors.toList()));
        String paramsSignature = String.join("", invocableDefinition.getParameters().stream().map((dp) -> dp.getType().jvmType().getSignature()).collect(Collectors.toList()));
        String methodDescriptor = "(" + paramsDescriptor + ")" + invocableDefinition.getReturnType().jvmType().getDescriptor();
        String methodSignature = "(" + paramsSignature + ")" + invocableDefinition.getReturnType().jvmType().getSignature();
        // TODO consider exceptions
        int modifiers = ACC_PUBLIC;
        if (isStatic) {
            modifiers = modifiers | ACC_STATIC;
        }
        MethodVisitor mv = cw.visitMethod(modifiers, invocableName, methodDescriptor, methodSignature, null);

        //addDefaultParamAnnotations(mv, invocableDefinition.getParameters());

        mv.visitCode();

        // Add local variables: they are necessary for supporting named parameters and useful for debugging
        Label start = new Label();
        Label end = new Label();
        mv.visitLabel(start);
        for (FormalParameter formalParameter : invocableDefinition.getParameters()) {
            int index = localVarsSymbolTable.add(formalParameter.getName(), formalParameter);
            mv.visitLocalVariable(formalParameter.getName(),
                    formalParameter.getType().jvmType().getDescriptor(),
                    formalParameter.getType().jvmType().getSignature(),
                    start,
                    end,
                    index);
        }

        compilationOfExpressions.compile(invocableDefinition.getBody()).operate(mv);

        // add implicit return when needed
        if (invocableDefinition.getReturnType() instanceof UnitTypeUsageNode) {
            // TODO do not add if there is already a return at the end
            new ReturnVoidBS().operate(mv);
        }

        mv.visitLabel(end);
        // calculated for us
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        localVarsSymbolTable = null;
    }
}
