package org.ogu.lang.compiler;

import org.objectweb.asm.ClassWriter;
import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Compilation phase
 * Created by ediaz on 21-01-16.
 */
public class Compilation {

    private static final int JAVA_8_CLASS_VERSION = 52;

    private SymbolResolver resolver;
    private ErrorCollector errorCollector;
    private Options options;
    private ClassWriter cw;

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

        if (options.isDebug()) {
            for (Node node : module.getChildren()) {
                Logger.debug("Node: "+node+" context: "+node.contextName());
            }
        }

//        classFileDefinitions.add(compileProgram(module));
        return classFileDefinitions;
    }
  /**
    private ClassFileDefinition compileProgram(OguModule module) {

        =new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String canonicalName = module.getNameDefinition().contextName();
        String internalName = JvmNameUtils.canonicalToInternal(canonicalName);
        String classSignature = "L" +
                Logger.debug("CanonicalName = [" + canonicalName + "]");
        cw.visit(JAVA_8_CLASS_VERSION, ACC_PUBLIC + ACC_SUPER, internalName, null, "java/lang/Object", null);
    }
   **/
}
