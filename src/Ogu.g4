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
    | classdef { println("class"); }
    | traitdef { println("trait"); }
    | var {println("var"); }
    | val {println("val"); }
    | typedef { println("type"); }
    | def { println("def"); }
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

classdef : 'class' TID  
( (gen_class_args)? '(' (class_args)? ')'  (class_extends)? (class_traits)? ('=' (NL)* class_decls)? 
| '+=' (NL)* class_decls
);

gen_class_args : '{' (gen_class_constraints)? TID (',' TID)* '}' ;

gen_class_constraints :  gen_class_constraint (',' gen_class_constraint)* '=>' ;

gen_class_constraint : TID ':' tid ;

class_args: class_simple_arg ((';'|',') class_simple_arg)* ;	

class_simple_arg : ('val'|'var')? ID (',' ID)*  ':' type ;

class_extends : '>' constructor_call ;

class_traits : '~' tid ('~' tid)* ;

class_decls : '{' (NL)* (class_decl)* '}' ;

class_decl
	:
	(  def { println ("class def"); }
	|  val { println ("class val"); }
	|  var { println ("class var"); }
	|  expression { println ("class statement"); }
	|  constructor_def
	) 
	(sep)*
	;

constructor_def
	: 'constructor' TID '(' (class_args)? ')' ':' constructor_call (NL)* (block_expression)?
	;

traitdef : 'trait' TID (trait_extends)? (trait_traits)? ('=' (NL)* trait_decls)? ; 

trait_extends : '>' tid ;

trait_traits : '~' tid ('~' tid)* ;


trait_decls
	: '{' (NL)* trait_decl* '}'
	;

trait_decl
	:
	(  def { println ("trait def"); }
	|  val { println ("trait val"); }
	|  var { println ("trait var"); }
	) 
	(sep)*
	;

var : 'var' vid  
    (':' type ('=' expression)? 
    | (':' (type)?)? ('=' expression)
    );

val : 'val' vid (':' (type)?)? '=' expression;

vid : ID | '(' vidt (',' vidt)* ')' ;

vidt : ID (':' type)? ;

type
	: '[' type ']'
	| '('  type (',' type)* ')'
	| TID '(' type (',' type)* ')'
	| type ('|' type)+
	| type '->' type
	| basic_type 
	;

basic_type
	: tid ('{' tid (',' tid)* '}')?
	;

typedef : 'type' TID (gen_class_args)? '=' (type | enum_values) ((NL)* '|' type)*;

enum_values : ID ('|' ID)* ;

def :  'def' (func_prototype ((NL)* func_def)* | func_def ) ;

func_prototype
	: ID '::' (prototype_constraints)? prototype_arg '->'  prototype_arg ('->' prototype_arg)* # FuncProt 
	;

prototype_constraints
	: '(' prototype_constraint_list ')' '=>' 
	| prototype_constraint_list '=>'
	;

prototype_constraint_list
	: prototype_constraint (',' prototype_constraint)* ;

prototype_constraint
	: ID ':' type
	;

prototype_arg
	: '[' prototype_arg ']'
	| '(' prototype_arg (',' prototype_arg)* ')' 
	| prot_type
	;

prot_type	: type | ID ;

func_def
	: ID (func_arg (func_arg)*)?  (NL)* ( func_simple_body | func_guards ((NL)* func_guards)* | block_expression ) (func_where)? { println("FUNCDEF"); } 
	;

func_simple_body : (':' ('lazy')? (type)?)? '=' (NL)* func_body  ;


func_arg
	: ID ':' type?
	| '(' (func_arg (',' func_arg)*)? ')'
	| TID
	| expression
	;

func_guards
	: '|' expression (':' ('lazy')? (type)?)? '=' func_body
	| '|' 'otherwise' (':' ('lazy')? (type)?)? '=' func_body
	;


func_body : 'abstract' | expression | block_expression ;


func_where 
	: (NL)? 'where' where_expr ( (';' | (NL)* 'and') (NL)* where_expr)* { println("where"); }
	;

where_expr
	: vid (':' (type)?)? '=' expression
	| func_def
	;



expression  
	: 'if' expression (NL)? 'then' (NL)* (expression (NL)?|block_expression)  'else' (NL)* (expression|block_expression) { println("if");}
	| 'for' (set_expr | list_expression) (NL)? 'do' (NL)? (expression | block_expression) { println("for"); }
	| 'when' expression  'do' (NL)* (expression|block_expression)
	| 'unless' expression  'do' (NL)* (expression|block_expression)
	| 'while' expression  'do' (NL)? (expression|block_expression) 
	| case_expression
	| let_expression
	| 'not' expression
	| 'yield' expression
	| '-' expression
	| '+' expression
	| expression '|>' expression
	| expression '<|' expression
	| expression '@' expression // at for array access
	| expression '^' expression
	| expression '*' expression
	| expression '/' expression
	| expression '%' expression
	| expression '+' expression
	| expression '-' expression
	| expression '&&' expression
	| expression '||'  expression
	| expression 'in' expression
	| expression 'not' 'in' expression
	| expression ('==' expression)
	| expression ('<>' expression)
	| expression '>' expression
	| expression '<' expression
	| expression '>=' expression
	| expression ('<=' expression)
	| expression '..' (expression)?
	| expression 'shl' expression
	| expression 'shr' expression
	| expression 'bitand' expression
	| expression 'bitor' expression
	| expression 'bitxor' expression
	| '(' expression_list ')'
	| '[' (list_expression)? ']'
	| expression '::' expression
	| expression '++' expression
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

constructor_call
	: tid '(' (ctor_args)? ')'
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
	  block_statement{println("block_statment 1");} 
	  ( block_statement{println("block statement*");})* 
	  '}'{println("BLOCK");}
	;

block_statement
	: 
	( var { println("var in block"); } 
	| val { println("val in block"); } 
	| expression { println ("expression in block"); }
	)
	(sep)*
	;

case_expression
	: 'case' expression 'of' (case_block | (NL)? case_selector_list) { println ("case expression"); }
	;

case_block
	: case_selector (sep case_selector)* (sep)*  ;

case_selector
	: expression_list '->' expression
	;

case_selector_list
	: case_selector ((sep case_selector)* | case_block)
	;

let_expression
	: 'let' (NL)* let_expr ( (';' | (NL)* 'and') (NL)* let_expr)* 
      (NL)* 'in' expression
	{ println("let"); }
	;

let_expr
	: vid (':' (type)?)? '=' expression
	| func_def
	;

list_expression : list_element (',' list_element)* ;

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

