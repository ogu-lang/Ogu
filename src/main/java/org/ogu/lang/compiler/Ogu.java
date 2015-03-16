package org.ogu.lang.compiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.ogu.lang.antlr.*;

public class Ogu {
	public static void main(String[] args) throws Exception {

		ANTLRInputStream input = new ANTLRInputStream(System.in);

		OguLexer lexer = new OguLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		OguParser parser = new OguParser(tokens);

		ParseTree tree = parser.module();

		System.out.println(tree.toStringTree(parser));
	}
}