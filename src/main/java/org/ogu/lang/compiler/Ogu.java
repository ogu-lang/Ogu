package org.ogu.lang.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.antlr.*;
import com.beust.jcommander.JCommander;
import org.ogu.lang.classloading.ClassFileDefinition;
import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.OguModuleWithSource;
import org.ogu.lang.parser.ast.OguModule;
import org.ogu.lang.parser.ast.Position;
import org.ogu.lang.resolvers.*;
import org.ogu.lang.resolvers.compiled.JarTypeResolver;
import org.ogu.lang.resolvers.jdk.JdkTypeResolver;

public class Ogu {
	public static void main(String[] args) throws Exception {

		message(Compiler.VERSION);
		message(WELCOME);

        Options options = new Options();
        JCommander commander = null;
        try {
            commander = new JCommander(options, args);
        } catch (Throwable t) {
            error(ERROR_OPTIONS+t.getMessage());
            System.exit(1);
            return;
        }

        if (options.isHelp()) {
            message(HELP_MESSAGE);
            commander.usage();
            return;
        }

        if (options.getSources().isEmpty()) {
            message(ERROR_NO_FILES_TO_COMPILE);
            commander.usage();
            return;
        }

        org.ogu.lang.parser.Parser parser = new org.ogu.lang.parser.Parser();

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
		message(GOODBYE);
	}


    private static SymbolResolver getResolver(List<String> sources, List<String> classPathElements, List<OguModule> oguModules) {
        TypeResolver typeResolver = new ComposedTypeResolver(ImmutableList.<TypeResolver>builder()
                .add(JdkTypeResolver.getInstance())
                .addAll(classPathElements.stream().map((cp) -> toTypeResolver(cp)).collect(Collectors.toList()))
                .build());
        return new ComposedSymbolResolver(ImmutableList.of(new InModuleSymbolResolver(typeResolver), new SrcSymbolResolver(oguModules)));
    }

    private static TypeResolver toTypeResolver(String classPathElement) {
        File file = new File(classPathElement);
        if (file.exists() && file.isFile() && classPathElement.endsWith(".jar")) {
            try {
                return new JarTypeResolver(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException(classPathElement);
        }
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

    /***
	private static void parseFile(String arg) throws Exception  {

		message(COMPILING_FILE, arg);
		message(COMPILING);
		ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(arg));

		OguLexer lexer = new OguLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		OguParser parser = new OguParser(tokens);
		parser.removeErrorListeners();
		OguVerboseListener listener = new OguVerboseListener();
		parser.addErrorListener(listener);
		ParseTree tree = parser.module();
		println(tree.toStringTree(parser));
		message(COMPILING_END, arg);
		if (listener.getErrores() > 0) {
			message(COMPILING_HAS_ERRORS, arg, listener.getErrores());
		}
	}
     ***/

	public static void println(String str) {
		System.out.println(str);
		System.out.println();
	}

	public static void message(String msg, Object ... args) {

		println(String.format(msg, args));
	}

	public static void error(String msg) {

		System.err.println(msg);
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

    static final String ERROR = "Me sake la ñoña! ";
    static final String ERROR_OPTIONS = "Mi no entendió nah: ";
    static final String ERROR_NO_FILES_TO_COMPILE = "Noay Peleita Yika! Mi tanto burrío...";
    static final String HELP_MESSAGE = "Yo te allullo amiko mio:\n";
	static final String WELCOME = "Hola amiko mio de mi.";
	static final String GOODBYE = "Ke kapo el kompilador, nosierto?\nChau, chau amiko mio de mi.";
	static final String COMPILING_FILE = "Yiko Peleita! (compilando archivo '%s').";
	static final String COMPILING = "Akarruuuu!";
	static final String COMPILING_END = "Mi soi kapo (archivo '%s' compilado).\n";
	static final String COMPILING_HAS_ERRORS = "Archivo %s tiene %d errores\n";
}