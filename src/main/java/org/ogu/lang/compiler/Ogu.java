package org.ogu.lang.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.beust.jcommander.JCommander;
import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.OguModuleWithSource;
import org.ogu.lang.parser.ast.modules.ModuleNode;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.resolvers.*;
import org.ogu.lang.resolvers.compiled.JarTypeResolver;
import org.ogu.lang.resolvers.jdk.JdkTypeResolver;
import org.ogu.lang.util.Logger;
import org.ogu.lang.util.Messages;

import static org.ogu.lang.util.Feedback.*;
import static org.ogu.lang.util.Feedback.message;

public class Ogu {
	public static void main(String[] args) throws Exception {

		message(Compiler.VERSION);

        Options options = new Options();
        JCommander commander = null;
        try {
            commander = new JCommander(options, args);
        } catch (Throwable t) {
            error(ERROR_OPTIONS+t.getMessage());
            System.exit(1);
            return;
        }

        if (options.isVerbose()) {
            message(WELCOME);
        }

        if (options.isHelp()) {
            message(HELP_MESSAGE);
            commander.usage();
            return;
        }

        if (options.getSources().isEmpty()) {
            if (options.isVerbose()) {
                message(ERROR_NO_FILES_TO_COMPILE);
            }
            commander.usage();
            return;
        }

        Logger.configure(options);

        org.ogu.lang.parser.Parser parser = new org.ogu.lang.parser.Parser(options);

        List<OguModuleWithSource> oguModules = new ArrayList<>();
        for (String source : options.getSources()) {
            try {
                oguModules.addAll(parser.parseAllIn(new File(source)));
            } catch (FileNotFoundException e) {
                error(ERROR + e.getMessage());
                System.exit(1);
                return;
            }
        }


        SymbolResolver resolver = getResolver(options.getSources(), options.getClassPathElements(), oguModules.stream().map(OguModuleWithSource::getModule).collect(Collectors.toList()));

        Compiler instance = new Compiler(resolver, options);
        for (OguModuleWithSource oguModule : oguModules) {
            for (ClassFileDefinition classFileDefinition : instance.compile(oguModule.getModule(), new ErrorPrinter(oguModule.getSource().getPath()))) {
                saveClassFile(classFileDefinition, options);
            }
        }
        if (options.isVerbose()) {
            message(GOODBYE);
        }
	}


    private static SymbolResolver getResolver(List<String> sources, List<String> classPathElements, List<ModuleNode> moduleNodes) {
        TypeResolver typeResolver = new ComposedTypeResolver(ImmutableList.<TypeResolver>builder()
                .add(JdkTypeResolver.getInstance())
                .addAll(classPathElements.stream().map(Ogu::toTypeResolver).collect(Collectors.toList()))
                .build());
        return new ComposedSymbolResolver(ImmutableList.of(new InModuleSymbolResolver(typeResolver), new SrcSymbolResolver(moduleNodes)));
    }

    private static TypeResolver toTypeResolver(String classPathElement) {
        File file = new File(classPathElement);
        if (file.exists() && file.isFile() && classPathElement.endsWith(".jar")) {
            try {
                return new JarTypeResolver(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException(classPathElement);
    }


    private static void saveClassFile(ClassFileDefinition classFileDefinition, Options options) {
        File output = null;
        try {
            output = new File(new File(options.getDestinationDir()).getAbsolutePath() + "/" + classFileDefinition.getName().replaceAll("\\.", "/") + ".class");
            if (options.isVerbose()) {
                System.out.println(" [saving "+output.getPath()+"]");
            }
            output.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(classFileDefinition.getBytecode());
        } catch (IOException e) {
            System.err.println(ERROR + " escribiendo archivo "+output+": "+ e.getMessage());
            System.exit(3);
        }
    }


    private static class ErrorPrinter implements ErrorCollector {

        private String fileDescription;

        public ErrorPrinter(String fileDescription) {
            this.fileDescription = fileDescription;
        }

        @Override
        public void recordSemanticError(Position position, String description) {
            System.err.println(fileDescription + " at " + position + ": (semantic error) " + description);
        }
    }

    static final String ERROR = Messages.message("ogu.error");
    static final String ERROR_OPTIONS = Messages.message("ogu.error.options");
    static final String ERROR_NO_FILES_TO_COMPILE = Messages.message("ogu.error.no_files");
    static final String HELP_MESSAGE = Messages.message("ogu.help");
	static final String WELCOME = Messages.message("ogu.welcome");
	static final String GOODBYE = Messages.message("ogu.goodbye");
    static final String LEXONLY = Messages.message("ogu.lex_only");
}