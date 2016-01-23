grammar Ogu;

tokens { INDENT, DEDENT }

@header {
package org.ogu.lang.antlr;

import java.util.*;
import java.lang.Math;
import com.yuvalshavit.antlr4.DenterHelper;

}

@lexer::members {
  private final DenterHelper denter = new DenterHelper(NL,
                                                       OguParser.INDENT,
                                                       OguParser.DEDENT)
  {
    @Override
    public Token pullToken() {
      return OguLexer.super.nextToken();
    }
  };

  @Override
  public Token nextToken() {
    return denter.nextToken();
  }
}


@parser::members {
	static void println(String str) {
		System.out.println(str);
	}

	static void print(String str) {
		System.out.print(str);
	}

}

module : moduleHeader=module_header? module_exports* module_uses* module_body NL*;

module_header : 'module' name=module_name NL* ;

module_name : (parts+=TID) ('.' parts+=TID)* ;

module_exports : compiler_flags? 'exports' exports+=export_name (',' exports+=export_name)*  NL*;

export_name : TID | ID  ;

module_uses : compiler_flags? 'uses' imports+=module_name (',' imports+=module_name)* NL* ;

module_body : (members+=module_decl NL*)* ;

module_decl
    : expr
    | compiler_flags?   (def | let | var | val_def |  alias_def | trait_def | instance_def | type_def | data_def | enum_def | class_def )
    ;

alias_def : 'alias' alias_target '=' alias_origin NL*;

alias_target : alias_tid=TID | alias_id=ID;

alias_origin :  alias_origin_tid+=TID ('.' alias_origin_tid+=TID)* ('.' alias_origin_id=ID)? ;

trait_def : 'mut'? 'trait' TID ID (ID)* 'where' INDENT ((def|let|var|val_def) NL* )* NL* DEDENT   ;

instance_def : 'instance' TID type type* 'where' INDENT ((def|let|var|val_def) NL* )* NL* DEDENT   ;

compiler_flags :  compiler_flag+ ;

compiler_flag : '#' '{' ID (STRING)* '}' NL*;

enum_def : 'enum' TID '=' ID ('|' ID)* deriving? ;

data_def : 'data' func_def_constraints? TID (typedef_args)? '=' data_type_decl ;

class_def : 'mut'? 'class' func_def_constraints? TID ID* '(' class_args? ')' ('=' class_ctor | class_where)? ;

class_args : class_arg (',' class_arg)* ;

class_arg : ('var'|'val')? ID (',' ID)* ':' type ;

class_ctor : TID '(' expr_list? ')'  ;

class_where : 'where' INDENT ((def|let|var|val_def) NL* )* NL* DEDENT ;

type_def :  'type' TID (typedef_args)? ('=' type)?  ;

data_type_decl : type ('|' type)* deriving?
              | type  INDENT ('|' type NL*)*  deriving? NL* DEDENT
              ;

deriving : 'deriving' deriving_types | INDENT 'deriving' deriving_types NL* DEDENT ;

deriving_types :  '(' tid (',' tid)* ')' ;

typedef_args
	: typedef_args_constraints? ID (ID)*
	;

typedef_args_constraints
	: '(' typedef_arg_constraint_list ')' '=>'
	;

typedef_arg_constraint_list
	: typedef_arg_constraint (',' typedef_arg_constraint)*
	;

typedef_arg_constraint
	: ID (',' ID)* ':' type
	;

enum_values : ID ((NL)* '|' ID)* ;

def :
    'def' func_id ':' (func_def_constraints)?
    (func_def_arg (<assoc=right> '->' func_def_arg)* ('->' '!')? 
    | <assoc=right>'->' func_def_arg
    |'->' '!');

var :  'var' vid (':' type)? ('=' expr)? ;

vid : ID | '(' vidt (',' vidt)* ')' ;

vidt : ID (':' type)? ;

func_def_constraints : '(' func_def_constraint_list ')' '=>' ;

func_def_constraint_list : func_def_constraint (',' func_def_constraint)* ;

func_def_constraint : ID ':' type ;

func_def_arg
	: '(' ')'
	| '[' func_def_arg ']'
	| '(' func_def_arg (',' func_def_arg)* ')'
	| '(' func_def_arg (<assoc=right> '->'func_def_arg)+ ')'
	| '{' func_def_arg ('->' func_def_arg)* '}'
	| ID
	| TID (TID|ID)*
	;

let
	: 'let' (lid|op) let_arg* let_expr
	| 'let' '(' lid (',' lid)* ')' '=' expr
	;

val_def
    :  'val' val_id=ID (':' type)? ('=' expr)
    | 'val' '(' lid (',' lid)* ')' '=' expr ;


lid : ID | ID ':' type ;

let_arg
    : let_arg_atom
    | '[' let_arg_atom? (',' let_arg_atom)* ']'
    | '(' let_arg_atom (',' let_arg_atom)* ')'
    | '(' let_arg_atom ('::' let_arg_atom)* ')'
    ;

let_arg_atom
    : lid | atom | TID (ID|let_arg)* ;

let_expr : '=' ( let_block | expr let_where? )
         |  guards where?
         ;

guards : INDENT (guard NL*)*  (guard_otherwise NL*)?  (where NL*)? DEDENT;

guard : '|' expr (expr)* '=' (expr|let_block) NL*  ;

guard_otherwise
	  : '|' 'otherwise' '=' (expr|let_block) NL*
	  ;

let_where : INDENT where NL* DEDENT ;

where : 'where' where_expr? (INDENT (where_expr NL*)+ DEDENT)? ;

where_expr
	: ID let_arg* NL* let_expr
	| '(' lid (',' lid)* ')' '=' expr
	;


let_block
    : INDENT (let_decl NL*)+ (where NL*)? DEDENT
    ;

let_decl
    : let | var | expr ;

block : INDENT (let_decl NL*)* DEDENT ;

type : '[' type ']'
     | '(' ')'
     | '(' type (',' type)* ')'
     | '{' ID ':' type (',' ID ':' type)* '}' // structs
     | TID '{' ID ':' type (',' ID ':' type)* '}' // structs
     | '{' type  '->' type '}'
     | type '->' type (<assoc=right>'->' type)*
     | tid (tid|ID|type)*
     | ID
     ;

tid : TID ('.' TID)* ;



func_id : ID | op ;

op : '@' | '+' | '-' | 'and' | 'or' | 'not' | 'yield' | '*' | '/' | '//' | '^' | '|>' | '<|'
	| 'in' | 'not' 'in' | '==' | '/='
	| '|' | '||' | '&&' | '++' | '::'
	| '>' | '<'  | '>=' | '<=' 
	| '..' | '...' 
	;

expr  
	: if_expr
	| 'for' (set_expr | list_expr) do_expression
    | 'case' expr 'of' case_guards
	| 'when' expr  do_expression
	| 'while' expr do_expression
	| 'unless' expr  do_expression
	| 'let' ID '=' expr (',' ID '=' expr)* ('in' expr)?
	| '\\' lambda_args? '->' (expr|block)
	| 'yield' expr
	| 'recur' expr*
	| '-' expr
	| '+' expr
	| 'not' expr
	| expr '..' (expr)?
	| expr '...'
	|<assoc=right> expr '^' expr
	| expr '*' expr
	| expr '/' expr
	| expr '%' expr
	| <assoc=right> expr '::' expr
	| <assoc=right> expr '++' expr
	| <assoc=left> expr '|>' expr
	| <assoc=right> expr '<|' expr
	| expr '+' expr
	| expr '-' expr
	| expr '==' expr
	| expr '/=' expr
	| expr '<'  expr
	| expr '<=' expr
	| expr '>'  expr
	| expr '>=' expr
	| expr 'in' expr
	| expr 'not' 'in' expr
	| expr '&&' expr
	| expr 'and' expr
	| expr '||' expr
	| expr 'or' expr
	| '(' op (expr)* ')'
	| '(' expr_list? ')'
	| '[' (list_expr)? ']'
	| '{' map_expr? '}'
	| expr '<-' expr
	| expr '@' expr
	| '$' ID
	| ID
	| function=func_name (params+=expr)+
	| qual_function=qual_func_name (params+=expr)*
	| set_expr
	| literal=atom
	;

func_name : name=ID;

qual_func_name : qual+=TID ('.' qual+=TID)* ('.' name=ID)? ;

if_expr : 'if' expr then_part ;

then_part : 'then' (expr  else_part|expr? then_block else_part) | INDENT 'then' (expr NL* else_part |expr? then_block else_part) NL* DEDENT ;

else_part : 'else' (expr|expr? else_block) ;

then_block : INDENT (let_decl NL*)+ NL* DEDENT ;

else_block : INDENT (let_decl NL*)+ DEDENT ;

case_guards : INDENT case_guard NL* (case_guard NL*)* DEDENT ;

case_guard : expr '=>' expr ;

expr_list
    : expr (',' expr)*
    ;

map_expr
    : expr '->' expr (',' expr '->' expr)*
    | ID '=' expr (',' ID '=' expr)*
    ;

atom : INT | FLOAT | string_literal=STRING | CHAR | DATE ;

list_expr : list_element (',' list_element)* ;

list_element : expr ('|' set_expr (',' set_expr)*)? ;


set_expr : ID expr? '<-' expr
         | '(' ID expr? (',' ID expr? )* ')' '<-' expr
         | 'set' ID expr? '=' expr ;

lambda_args
	: lambda_arg lambda_arg*
	;

lambda_arg
	: ID (':' type)?
	| '(' (lambda_arg (',' lambda_arg)*)? ')'
	;		

do_expression
	: 'do' (expr | block)
	;

ID : [_a-záéíóúñ][_\-a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ('!'|'?')?;

TID : [A-ZÁÉÍÓÚÑ][_\-a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ;

STRING : '"' (ESC|.)*? '"' ;

CHAR : '\'' (ESC|.) '\'' ;

fragment
ESC : '\\"' | '\\\\' ;

DATE : '#' DIGIT (DIGIT|'-')* ('T' DIGIT (DIGIT|':')* ('.' DIGIT*)*)? ('Z'|('+'|'-')DIGIT+(':' DIGIT+))?;

INT : DIGIT (DIGIT|'_')* ;

FLOAT : INT '.' DIGIT+ ;

fragment
DIGIT : [0-9] ;


LINE_COMMENT : ';' .*? '\r'? '\n' -> skip ;

NL: ('\r'? '\n' ' '*);

WS : ' '+ -> skip;
