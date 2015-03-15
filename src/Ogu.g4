grammar Ogu;

@header {
	import java.util.*;
	import java.lang.Math;
}


@parser::members {
	static void println(String str) {
		System.out.println(str);
	}

	static void print(String str) {
		System.out.print(str);
	}

}

program : (NL)*
        ( module_decl
        | (decl)*
        );

module_decl
	: 'module' TID ('.' TID)* (NL)* 
	(  '{' (NL)? (decl) * '}'
 	|  (decl)*
 	)
	;

decl :
    ( import_module { println("import module"); }
    | alias_decl { println("alias"); }
    | instancedef { println("instance"); }
    | var {println("var"); }
    | val {println("val"); }
    | typedef { println("type"); }
    | classdef { println("class");}
    | traitdef { println("trait"); }
    | def { println("def"); }
    | expression { println("decl expression"); }
    | do_expression { println ("decl do expression"); }
	| block_expression { println ("decl block expression"); }
    ) (sep)*
    ;

sep : ';' | NL;


import_module
	: 'uses' tid  (',' tid)*
	;

alias_decl
	: 'alias' (TID '=' tid | ID '=' id)
	;


tid : TID ('.' TID)* ;

id : (TID '.')* ID ;


instancedef
	: 'instance' TID instance_args instance_decls ;

instance_args
	: instance_arg (',' instance_arg)* ;


instance_arg
	: (tid | expression)
	| ID ':' (tid|expression)
	;

instance_decls
	: (NL)* '=' '{' (NL)* (instance_decl)* '}'
	;

instance_decl
	: def sep*
	;

var : 'var' vid  
    (':' type ('=' expression)? 
    | (':' (type)?)? ('=' expression)
    );

val : 'val' vid (':' (type)?)? '=' expression;

vid : ID | '(' vidt (',' vidt)* ')' ;

vidt : ID (':' type)? ;

type
	: '[' type ((':') type )? ']'
	| '('  (type (',' type)*)? ')'
	| tid '(' type (',' type)* ')'
	| type ('|' type)+
	| type '->' type
	| basic_type (basic_type)*
	| ID
	;

basic_type
	: tid | ID  
	;

typedef : 'type' TID (typedef_args)? '=' 
		( enum_values 
		| type ((NL)* '|' type)*
		);

traitdef : 'trait' TID typedef_args ('=' traitdef_body)? ;

traitdef_body : '{' (NL)* traitdef_method* '}' ;

traitdef_method
	: func_proto_def sep*;

classdef : 'class' TID typedef_args? class_constructor_args? 
		('=' 
		( enum_values 
		| type ((NL)* '|' type)*
		| TID '(' ctor_args? ')'
		|
		)
		classdef_body?)?;

class_constructor_args : '(' (class_ctor_arg (';' class_ctor_arg)*)? ')' ;

class_ctor_arg : ('val'|'var')? ID (',' ID)* ':' type ;

classdef_body : '{' (NL)* (classdef_body_decl)* '}' ;

classdef_body_decl
	: (def|var|val|expression|do_expression|block_expression) sep*
	;


typedef_args
	: (typedef_args_constraints)? ID (ID)*
	;

typedef_args_constraints
	: typedef_arg_constraint_list '=>'
	| '(' typedef_arg_constraint_list ')' '=>'
	;

typedef_arg_constraint_list
	: typedef_arg_constraint (';' typedef_arg_constraint)*
	;

typedef_arg_constraint
	: ID (',' ID)* ':' type
	;

enum_values : ID ((NL)* '|' ID)* ;

def :  func_proto_def | func_clasic_def;

func_proto_def : func_proto_def_header
	
	((NL)* func_def_proto)* 
	;

func_proto_def_header : 'def' func_id '::' (prototype_constraints)? prototype_arg '->'  prototype_arg ('->' prototype_arg)* ;

prototype_constraints
	: '(' prototype_constraint_list ')' '=>' 
	| prototype_constraint_list '=>'
	;

prototype_constraint_list
	: prototype_constraint (';' prototype_constraint)* ;

prototype_constraint
	: ID (',' ID)* ':' type
	;

prototype_arg
	: '[' prototype_arg ']'
	| '(' prototype_arg (',' prototype_arg)* ')' 
	| prot_type
	;

prot_type	: type | ID ;

func_def_proto
	: func_id (expression|func_arg)* 
	     ('=' (NL)* func_body 
	     | (NL)* func_guards ((NL)* func_guards)*
	     )
	     (func_where)?
	;

func_clasic_def
	: 'def' func_id (func_arg (func_arg)*)?  (NL)* ( func_simple_body | func_guards ((NL)* func_guards)* | block_expression | do_expression ) func_where? 
	;

func_simple_body : ('->' (type)?)? '=' (NL)* func_body ;

func_body : ( 'abstract' | expression | block_expression ) ;

func_id : ID | binop | unop ;

func_arg
	: ID ':' type
	| '(' (func_arg (',' func_arg)*)? ')'
	| TID
	| expression
	;

func_guards
	: '|' expression '=' func_body
	| '|' 'otherwise' '=' func_body
	;


func_where 
	: (NL)* 'where' (NL)*  where_expr ((sep)* where_expr)* 
	;

where_expr
	: vid (':' (type)?)? '=' expression
	| func_def_proto
	;



expression  
	: 'if' expression (NL)? 'then' (NL)* (expression (NL)?|block_expression)  'else' (NL)* (expression|block_expression) 
	| 'for' (set_expr | list_expression) (NL)? do_expression 
	| 'when' expression  do_expression
	| 'unless' expression  do_expression
	| 'while' expression  do_expression 
	| case_expression
	| let_expression
	| unop expression
	| expression binop expression
	| '(' binop (expression)* ')'
	| '(' expression_list ')'
	| '[' (list_expression|map_expressions)? ']'
	| expression '[' expression ']'
	| expression '..' (expression)?
	| ID ('.' ID)+
	| ID (expression)*
	| ID '=' expression
	| constructor_call
	| INT
	| FLOAT
	| STRING
	| CHAR 
	| DATE
	;

unop : '+' | '-' | 'not' | 'yield' ;

binop : '+' | '-' | '*' | '/' | '%' | '>>' | '^' | '|>' | '<|'
	| '&&' 	| '||' 	| 'in' | 'not' 'in' | '==' | '/='
	| '::' | '++'
	| '<>' 	| '>' 	| '<' | '>=' | '<=' | 'shl'  | 'shr' 
	| 'bitand'  | 'bitor'  	| 'bitxor' 
	;

constructor_call
	: tid '(' (ctor_args)? ')'
	| tid expression*
	;

ctor_arg
	: (ID ':')? expression
	;

ctor_args
	: ctor_arg (',' ctor_arg)*
	;

block_expression
	: '{' 
	  (sep)*
	  block_statement 
	  block_statement* 
	  '}'
	| 'do' expression
	;
do_expression
	: 'do' (NL)* (expression|block_expression)
	;

block_statement
	: 
	( var 
	| val 
	| expression 
	)
	(sep)*
	;

case_expression
	: 'case' expression 'of' (NL)* case_selector_list { println ("case expression"); }
	;


case_selector_list
	: case_selector (sep+ case_selector)* ;


case_selector
	: expression '->' expression
	;


let_expression
	: 'let' (NL)* let_expr ( sep* let_expr)* 
      (NL)* 'in' expression
	;

let_expr
	: vid (':' (type)?)? '=' expression
	| func_def_proto
	;

list_expression : list_element (',' list_element)* ;

map_expressions : map_expression (',' map_expression)* ;

map_expression : expression ':' expression ;

list_element : expression ('|' set_expr (',' set_expr)*)? ;

set_expr : expression ('<-' expression)? ;

expression_list : expression (',' expression)* ;

idList : ID (',' ID)* ;


ID : [_a-záéíóúñ][_a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ('!'|'?')?;

TID : [A-ZÁÉÍÓÚÑ][_a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ;


STRING : '"' (ESC|.)*? '"' ;

CHAR : '\'' (ESC|.) '\'' ;

fragment
ESC : '\\"' | '\\\\' ;

DATE : '#' DIGIT+;

INT : DIGIT+ ;

FLOAT : DIGIT+ '.' DIGIT+ ;

fragment
DIGIT : [0-9] ;

WS : ([ \t])+ -> skip;

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ;

NL : ('\r'? '\n' (' ')*);

