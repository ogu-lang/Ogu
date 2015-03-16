package org.ogu.lang.compiler;

import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.ogu.lang.antlr.*;

public  class OguVerboseListener extends BaseErrorListener {

	@Override
	public void syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		Ogu.error(ERROR_BEGIN);
		Ogu.error("\trule stack: "+stack);
		Ogu.error("\tline "+line+":"+charPositionInLine+" at "+offendingSymbol+": "+msg+"\n");
		Ogu.error(ERROR_END);
	}

	private static final String ERROR_BEGIN = "Mi tanto nojao!\n(tienes errores):\n";
	private static final String ERROR_END = "Te boya sakar la n~on~a kabro chiko!\n";
}