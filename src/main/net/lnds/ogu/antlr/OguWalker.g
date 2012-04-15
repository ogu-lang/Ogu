tree grammar OguWalker;

options {
	tokenVocab=Ogu;
	ASTLabelType=CommonTree;
}
@header {
	package net.lnds.ogu.antlr.parser;
}

@members {

    public static void println(String str) {
        System.out.println(str);
    }

    public static void print(String str) {
        System.out.print(str);
    }

    public static void fatal(String err) {
        System.err.println(err);
        System.exit(-1);
    }
}

prog : preamble top_level_decl* ;

preamble
	: ((package_header)=>package_header)? import_package*;

package_header
	: ((annotation)=>annotation)* package_label ;

package_label
	: ^(PACKAGE path)
	  { println ("package "+$path.text+";"); }
	;

top_level_decl
	: type_def | func_decl
	//(func_decl | class_decl | object_decl | type_def | v_decl) 
    ;


import_package
	: ((annotation)=>annotation)* import_label;

import_label
	: ^(IMPORT path DOT MULT) 
	  { println("import "+$path.text+".*;"); }
	| ^(IMPORT path AS t=T_ID)
	  { println("import "+$path.text+" as "+$t.text+";"); }
	;

path
	: ^(PATH name*) ;

name : V_ID | T_ID ;

simple_name : V_ID;


annotation	: ^(ANNOTATION simple_name annotation_arg*)	;

annotation_arg
	: ^(ANNOTATION_ARG literal V_ID?) ;


type_def
	: ^(TYPE id=T_ID tp=type_parameters t=type)
	  { println("typedef "+$t.text+$tp.text+" as "+$id.text+";"); }
	| ^(TYPE id=T_ID  t=type)
	  { println("typedef "+$t.text+" as "+$id.text+";");  }
	;

type_parameters
	: ^(TYPE_PARAMS T_ID+) ;


type : type_decl QUESTION? ;

type_decl
	: ^(ARROW primary_type+)
	| primary_type
	;

primary_type
	: sequence_type ((array_sufix)=>array_sufix)?
	;

sequence_type
	: bracket_type
	| tuple_type
	| type_union
	;

type_union
	: type_intersection 
	| ^(BAR type_intersection type_intersection+)
	;

type_intersection
	: atom_type 
	| ^(AMPERSAND atom_type atom_type+)
	;

atom_type : generic_type  ((ISA|TILDE) generic_type)?;

bracket_type : ^(LBRACKET (type (COLON type)?)? );

tuple_type : ^(LPAREN type_list) ;

generic_type : T_ID (generic_sufix)? ;

generic_sufix : ^(LCURLY type_list) ;


array_sufix : ^(LBRACKET array_dim?);

array_dim : INT+;

type_list
	: type+;

literal	: INT | FLOAT | STRING | TRUE | FALSE | NULL ;