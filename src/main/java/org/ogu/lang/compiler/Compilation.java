package org.ogu.lang.compiler;

import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguModule;
import org.ogu.lang.resolvers.SymbolResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ediaz on 21-01-16.
 */
public class Compilation {

    private SymbolResolver resolver;
    private ErrorCollector errorCollector;

    public Compilation(SymbolResolver resolver, ErrorCollector errorCollector) {
        this.resolver = resolver;
        this.errorCollector = errorCollector;
    }

    public List<ClassFileDefinition> compile(OguModule module) {
        boolean valid = module.validate(resolver, errorCollector);

        if (!valid) {
            return Collections.emptyList();
        }

        List<ClassFileDefinition> classFileDefinitions = new ArrayList<>();

        for (Node node : module.getChildren()) {

        }

        return classFileDefinitions;
    }
}
