grammar Ogu;

options {
	output = AST;
	ASTLabelType = CommonTree;
	tokenVocab=Ogu;
}

tokens {
	BLOCK;
	EXPR;
    PFU;
    ATOM;
    FC;
    ANNOTATION;
    ANNOTATION_ARG;
    LITERAL;
}

@header {
	package net.lnds.ogu.antlr.parser;
}

@lexer::header {
    package net.lnds.ogu.antlr.parser;    
}

@lexer::members {
   int parenLevel = 0;
   int curlyLevel = 0;
   int bracketLevel = 0;
 
   boolean ignoreNewLines() {
        if (curlyLevel > 0)
            return false;
        if (parenLevel > 0 || bracketLevel > 0)
            return true;
        return false;
   }


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

prog 
@after { println("PROG: "+$prog.tree.toStringTree()+"\n"); }
: (nl)? preamble (top_level_decl)*;

preamble
	: package_header? (import_package)*;

package_header
	: (annotation)* PACKAGE^ path sep? ;

import_package
	: (annotation)* IMPORT^ path (DOT! MULT | AS^ T_ID) sep? ;

top_level_decl
	: (annotation)*
	(func_decl | class_decl | object_decl | type_def | v_decl) 
    sep?;



class_decl : CLASS^ T_ID ((type_arguments)=> type_arguments)? value_arguments?
             class_body?;


object_decl : OBJECT (V_ID|T_ID);


class_body : ASSIGN! LCURLY! 
    class_elements
    RCURLY! ; 


class_elements 
    : semi? class_element (semi class_element)* semi?
    | semi? 
    ;

class_element
    : (annotation)* (func_decl|v_decl) ;

type_def : TYPE^ T_ID type_parameters? ASSIGN^ type ;

type_parameters
	: LCURLY t_id_list RCURLY
	;

annotation
	:  n=name
	  ( l=literal ->^(ANNOTATION $n $l)
	  | LPAREN a+=annotation_arg (COMMA a+=annotation_arg)* RPAREN ->^(ANNOTATION $n $a+)
	  | LCURLY nl? a+=annotation_arg (sep a+=annotation_arg)* sep? RCURLY ->^(ANNOTATION $n $a+)
	  )?
	  nl? 
    ;

    


annotation_arg
	: (v=V_ID (ASSIGN|COLON))? l=literal -> ^(ANNOTATION_ARG $v? $l)
	;

func_decl : DEF^ V_ID ((type_arguments)=> type_arguments)? func_body ;

func_body 
options { backtrack = true;}
	: (expr_list)* func_value
	|  type_list ARROW type (func_value)?
	| LPAREN (func_args)? RPAREN ( (block| func_value) | ARROW func_ret (func_value)?)?
	| COLON type (func_value | (COMMA type)* ARROW type )?
    ;

func_value
	: 
	( ((nl)=> nl)? ASSIGN nl?  expr 
	| (func_guard)=> func_guard+ func_otherwise?
	)
	(where_clause)?	
	;

func_guard
	: nl BAR expr ASSIGN nl? expr	 ;

func_otherwise
	: nl BAR OTHERWISE  ASSIGN nl?  expr ;
	
where_clause
	: ((nl)=>nl)? WHERE^ nl?
	 where_bindings 
	;

func_args
	: (type_list)=> type_list
	| func_arg (COMMA func_arg)*
	;

func_ret
	: type
	| LPAREN func_ret_atom RPAREN 
	;


func_ret_atom
	: V_ID COLON type
	;

func_arg
	: v_id_list COLON type ;

type_list
	: type (COMMA type)*;

type
options { backtrack = true;}
	: func_type QUESTION?
	| primary_type QUESTION?
	;

func_type
	: primary_type (COMMA primary_type)* ARROW^ primary_type
	;

primary_type
	: sequence_type (array_sufix)?
	;

sequence_type
	: bracket_type
	| tuple_type
	| type_union
	;

type_union
	: type_intersection (BAR^ type_intersection)*
	;

type_intersection
	: atom_type (AMPERSAND^ atom_type)*
	;

atom_type : generic_type  ((ISA|TILDE) generic_type)?;

bracket_type : LBRACKET^ (type (COLON type)?)? RBRACKET! 	;

tuple_type : LPAREN type_list RPAREN ;

generic_type : T_ID (generic_sufix)? ;

generic_sufix : LCURLY^ type_list RCURLY! ;


array_sufix : LBRACKET^ (array_dim)? RBRACKET! ;

array_dim : INT (COMMA INT) ;

v_id_list : V_ID ((COMMA V_ID)=> COMMA V_ID)* ;

t_id_list : T_ID (COMMA T_ID)* ;

path
	: V_ID (DOT V_ID)* (DOT T_ID)? ;

expr_list
options {backtrack = true; }
	: expr ((COMMA)=> COMMA expr)*
	;

expr :	or_expr ;

or_expr  : and_expr ((OR)=> OR^ and_expr)* ;

and_expr : eq_expr ((AND)=> AND^ eq_expr)* ;

eq_expr  : comp_expr ((EQ|NE)=> (EQ|NE)^ comp_expr)* ;

eq_op : EQ | NE ;

comp_expr : named_infix ((LT|GT|LE|GE)=> (LT^ | GT^ | LE^ | GE^) named_infix)* ;

named_infix
options {backtrack = true;}
	: elvis_expr ((LEFT_ARROW)=> (LEFT_ARROW) elvis_expr)* 
	| elvis_expr ( (IS (NOT)?| BANGIS)=> (IS ((NOT)=>NOT)?| BANGIS) elvis_expr )* 	
	;


elvis_expr : infix_func_call ((ELVIS)=> ELVIS^ infix_func_call)* ;

infix_func_call : range_expr ((simple_name)=> simple_name range_expr)* ;

range_expr : cons_expr ((DOTDOT)=> DOTDOT^  cons_expr)* ;

cons_expr : add_expr ((CONS )=> CONS^ add_expr)? ;

add_expr : mult_expr ((PLUS|CONCAT|MINUS)=> (PLUS^|CONCAT^|MINUS^) mult_expr)* ;

mult_expr : as_expr ((MULT|DIV)=> (MULT^|DIV^) as_expr)* ;

as_expr : power_expr ((type_op)=> type_op power_expr)* ;

power_expr : prefix_unary ((POWER)=> POWER^ prefix_unary)* ;

type_op : AS | AS QUESTION ;

prefix_unary 
	: (PLUS^|MINUS^|BANG^|NOT^)* postfix_unary ;


postfix_unary 
options {backtrack=true;}
    : (a=atom) ((postfix_op)=> p=postfix_op)* 
    ;



postfix_op 
options { backtrack = true; }
	: call_sufix 
	| array_access 
	| DOT postfix_unary 
	| QDOT postfix_unary 
	| QUESTION postfix_unary 
	| postfix_unary 
    | named_arg ((COMMA named_arg)=>COMMA^ named_arg)*
    | BAR expr_list 
    ;

named_arg
    : simple_name (ASSIGN|COLON) postfix_unary
    ;

call_sufix
options { backtrack = true; }
	: type_arguments value_arguments function_literal
	| type_arguments function_literal
	| value_arguments function_literal
	| value_arguments
	| function_literal
	;

function_literal
	: LCURLY    statements  RCURLY
	;

array_access : LBRACKET expr_list RBRACKET ;

value_arguments : LPAREN! val_args? RPAREN! ;

val_args : val_arg (COMMA val_arg)* ;

val_arg : (name (ASSIGN|COLON))? expr ;

type_arguments	: LCURLY type_list RCURLY ;

atom : name
     | literal
     | LPAREN! expr_list RPAREN!
     | LBRACKET^ (expr_list)? RBRACKET!
	 | function_literal
	 | THIS
	 | SUPER
	 | if_expr
	 | let_expr
     | case_expr
     | lambda_expr
     | for_expr
     | lazy_expr
	 ;

lazy_expr
    : LAZY expr ;

let_expr
options { backtrack = true; }
	: LET^ nl? let_bindings nl? IN^ nl? expr
    | LET^ nl? let_bindings;

case_expr
    : CASE^ expr OF! nl? 
        expr ARROW expr ((sep expr ARROW)=> sep expr ARROW expr)* ;



where_bindings : let_bindings ;

let_bindings
    : let_binding ((semi let_binding)=> semi let_binding)* ;

let_binding
options { backtrack = true; }
    : expr_list ASSIGN^ expr
    ;


if_expr
options { backtrack = true; }
	: IF^ LPAREN! expr RPAREN! nl? expr nl? (ELSE! nl? expr)
	| IF^ LPAREN! expr RPAREN! nl? expr
	;

for_expr
    : FOR^ LPAREN! (VAR^|VAL^)? simple_name (COLON^ type)? IN^ expr RPAREN! expr 
    ;

lambda_expr
    : LAMBDA lambda_args ARROW expr ;


lambda_args
    : lambda_arg ((COMMA lambda_arg)=> COMMA lambda_arg)* ;

lambda_arg
    : v_id_list (COLON type)?
    ;

statements
options { backtrack = true; }
	: semi? statement (semi statement)* semi?
    | semi?;

statement
	: expr (ASSIGN expr)?
	| v_decl
   ;


v_decl
	: (VAR^|VAL^) var_list 
     (COLON ASSIGN expr
     |COLON type (ASSIGN expr)?
     )
    ;

var_list
    : LPAREN v_id_list RPAREN
    | v_id_list
    ;

block : ((nl)=>nl)? LCURLY s=statements RCURLY ->^(BLOCK $s);


literal
	: INT^
	| FLOAT^
	| STRING^
	| TRUE^
	| FALSE^
	| NULL^
	;

name : V_ID | T_ID;

simple_name : V_ID ;

simple_name_list : simple_name (COMMA simple_name)* ;


// esto hay que mejorarlo, es un parche para ciertos bloques patológicos (ver test_block)
sep 
@init {
    int marker = input.mark();
}
: (SEMI!)+ (NL!)* 
| (NL!)+
| RCURLY { input.rewind(marker); }
| EOF!
;

semi
    : SEMI! | (NL!)+
    ;

nl : (NL!)+ ;


AS    		: 'as';
CASE        : 'case';
CLASS 	    : 'class' ;
DEF 		: 'def' ;

ELSE 		: 'else';
ELSIF 		: 'elsif';
FALSE       : 'false';
FOR      	: 'for';
IMPORT 		: 'import';
IF 			: 'if';
IN
 			: 'in' ;
IS 			: 'is';
LAZY        : 'lazy';
LET 		: 'let';
NOT			: 'not';
NULL 		: 'null';
OBJECT 		: 'object';
OF          : 'of';
OTHERWISE   : 'otherwise';
PACKAGE 	: 'package';
SUPER		: 'super';
THIS		: 'this';
TRUE		: 'true';
TYPE 		: 'type';
VAL 		: 'val';
VAR 		: 'var';
WHERE 		: 'where' ;



AND 		: '&&';
AMPERSAND 	: '&';

ARROBA	: '@' ;

MINUS		: '-';
ARROW		: '->';

EQ 			: '==';
ASSIGN 		: '=';

OR 			: '||';
BAR			: '|';

CONS		: '::';
COLON  		: ':';
COMMA  		: ',';
DOLLAR		: '$';

DIV			: '/';

GE 			: '>=';
GT     		: '>';

LE 			: '<=';
NE2         : '<>';
LEFT_ARROW  : '<-';
ISA         : '<:';
LT 			: '<';

LBRACKET	: '[';
LCURLY 		: '{' { ++curlyLevel; };
LPAREN		: '(' { ++parenLevel; };
MULT		: '*';

BANG		: '!';
BANGIS      : '!is';
NE 			: '!=';


CONCAT		: '++';
PLUS		: '+';
QDOT        : '?.';
ELVIS 		: '?:';
QUESTION	: '?';
RCURLY 		: '}' { --curlyLevel; };
RBRACKET	: ']';
RPAREN		: ')' { --parenLevel; };
SEMI 		: ';';
TILDE  		: '~';
POWER       : '^';

LAMBDA      : '\\';


fragment LOWER : 'a'..'z' ;

fragment UPPER : 'A'..'Z' ;

fragment LETTERS : 'a'..'z' | 'A'..'Z' | '_' ;

V_ID  :	('a'..'z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;

T_ID  :	('A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;


ESC_ID : '`' (LETTERS|DIGITS)+ '`' ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"' ))* '"'
    |  '\'' ( ESC_SEQ | ~('\\'|'\'' ))* '\''
    ;

fragment INT :  ; //:   '0'..'9'+
    

fragment DOTDOT : ;
fragment DOT : ;


FLOAT
    : DIGITS ( 
        { input.LA(2) != '.'}?=> '.' DIGITS EXPONENT?  { $type = FLOAT; }
        | EXPONENT { $type= FLOAT; }
        | {$type=INT;}
        )
    | '.' 
       (DIGITS EXPONENT? {$type = FLOAT; } 
       | '.' {$type = DOTDOT; } 
       | {$type=DOT;} 
       )
    
    ;

fragment DIGITS : ('0'..'9')+;

    
fragment
EXPONENT : ('e'|'E') (PLUS|MINUS)? ('0'..'9')+ ;    
    
fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;



COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN;}
    ;

NL : ('\r\n'| '\r'     | '\n') { if (ignoreNewLines()) $channel=HIDDEN; } ;

WS  :   ( ' '
        | '\t'
        | '\f'
        ) { $channel=HIDDEN;}
    ;

