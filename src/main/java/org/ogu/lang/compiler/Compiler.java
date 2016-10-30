package org.ogu.lang.compiler;

import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.resolvers.ResolverRegistry;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.List;

/**
 * Ogu Compiler
 * Created by ediaz on 20-01-16.
 */
public class Compiler {

    public static String VERSION = "Ogu compiler version 0.1.4 (Plunke)";

    private SymbolResolver resolver;
    private Options options;

    public Compiler(SymbolResolver resolver, Options options) {
        this.resolver = resolver;
        this.options = options;
    }


    public List<ClassFileDefinition> compile(ModuleNode module, ErrorCollector errorCollector) {
        ResolverRegistry.INSTANCE.record(module, resolver);
        return new Compilation(resolver, errorCollector, options).compile(module);
    }



}
