package org.ogu.lang.compiler;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.ogu.lang.antlr.*;

public class Ogu {
	public static void main(String[] args) throws Exception {
		message(VERSION);
		message(WELCOME);
		for (String arg : args) {
			parseFile(arg);
		}
		message(GOODBYE);
	}

	private static void parseFile(String arg) throws Exception  {
		message(COMPILING_FILE, arg);
		message(COMPILING);
		ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(arg));

		OguLexer lexer = new OguLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		OguParser parser = new OguParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(new OguVerboseListener());
		ParseTree tree = parser.module();
		println(tree.toStringTree(parser));
		message(COMPILING_END, arg);
	}

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

	static final String VERSION = "Ogu version 0.1.0";
	static final String WELCOME = "Hola amiko mio de mi.";
	static final String GOODBYE = "Ke kapo el kompilador, nosierto?\n\nChau, chau amiko mio de mi.";
	static final String COMPILING_FILE = "Yiko Peleita!\n(compilando archivo '%s')";
	static final String COMPILING = "Akarruuuu!";
	static final String COMPILING_END = "Mi soi kapo.\n(archivo '%s' compilado)\n";
}