package net.lnds.ogu.compiler;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import net.lnds.ogu.antlr.parser.*;

public class Ogu {
    public static void main(String[] args) throws Exception {
    	if (args.length == 0)
    		usage();
    	else {
            boolean gendot = false;
            int arg = 0;

    		//System.out.println("Ogu version 0.1-alpha");

            if (args[arg].equals("-dot")) {
                arg++;
                gendot = true;
            }
            if (arg == args.length)
                    usage();
            else {
    		  // System.out.println("Parsing: "+args[arg]);

	           ANTLRFileStream in = new ANTLRFileStream(args[arg], "UTF8");
    	       OguLexer lexer = new OguLexer(in);
    	       CommonTokenStream tokens = new CommonTokenStream(lexer);
    	       OguParser parser = new OguParser(tokens);
               OguParser.prog_return r = parser.prog();
    	       CommonTree t = (CommonTree) r.getTree();

               if (gendot) {
                  DOTTreeGenerator gen = new DOTTreeGenerator();
                  StringTemplate st = gen.toDOT(t);
                  System.out.println(st);
               } else 
                  System.out.println(t.toStringTree());

                CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
                nodes.setTokenStream(tokens);
                OguWalker walker = new OguWalker(nodes);
                walker.prog();
            }
	    }
    }

    public static void usage() {
    	System.out.println("usage: Ogu [-dot] file.ogu");
    }
}