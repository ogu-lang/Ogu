package net.lnds.ogu.compiler;

import org.antlr.runtime.*;
import net.lnds.ogu.antlr.parser.*;

public class Ogu {
    public static void main(String[] args) throws Exception {
    	if (args.length != 1)
    		usage();
    	else {
    		System.out.println("Ogu version 0.1-alpha");
    		System.out.println("Parsing: "+args[0]);

	        ANTLRFileStream in = new ANTLRFileStream(args[0]);
	        OguLexer lexer = new OguLexer(in);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        OguParser parser = new OguParser(tokens);
	        parser.prog();
	    }
    }

    public static void usage() {
    	System.out.println("usage: Ogu file.ogu");
    }
}