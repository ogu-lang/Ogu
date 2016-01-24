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

module_exports : decs=decorators? 'exports' exports+=export_name (',' exports+=export_name)*  NL*;

export_name : TID | ID  ;

module_uses : decs=decorators? 'uses' imports+=module_name (',' imports+=module_name)* NL* ;

module_body : (members+=module_decl NL*)* ;

module_decl
    : expr
    | decs=decorators?   (func_decl | func_def | var | val_def |  alias_def | trait_def | instance_def | type_def | data_def | enum_def | class_def )
    ;

alias_def : 'alias' alias_target '=' alias_origin NL*;

alias_target : alias_tid=TID | alias_id=ID;

alias_origin :  alias_origin_tid+=TID ('.' alias_origin_tid+=TID)* ('.' alias_origin_id=ID)? ;

trait_def : 'mut'? 'trait' TID ID (ID)* 'where' INDENT ((func_decl|func_def|var|val_def) NL* )* NL* DEDENT   ;

instance_def : 'instance' TID type type* 'where' INDENT ((func_decl|func_def|var|val_def) NL* )* NL* DEDENT   ;

decorators :  (dec+=decorator)+ ;

decorator : '#' '{' dec_id=ID (dec_args+=STRING)* '}' NL*;

enum_def : 'enum' TID '=' ID ('|' ID)* deriving? ;

data_def : 'data' func_decl_constraints? TID (typedef_args)? '=' data_type_decl ;

class_def : 'mut'? 'class' func_decl_constraints? TID ID* '(' class_args? ')' ('=' class_ctor | class_where)? ;

class_args : class_arg (',' class_arg)* ;

class_arg : ('var'|'val')? ID (',' ID)* ':' type ;

class_ctor : TID '(' expr_list? ')'  ;

class_where : 'where' INDENT ((func_decl|func_def|var|val_def) NL* )* NL* DEDENT ;

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

func_decl :
    'def' name=func_name_decl ':' (func_decl_constraints)?
      (arg+=func_decl_arg (<assoc=right> '->' arg+=func_decl_arg)*
      | '->'? func_decl_arg
      );

var :  'var' vid (':' type)? ('=' expr)? ;

vid : ID | '(' vidt (',' vidt)* ')' ;

vidt : ID (':' type)? ;

func_decl_constraints : '(' func_decl_constraint_list ')' '=>' ;

func_decl_constraint_list : func_decl_constraint (',' func_decl_constraint)* ;

func_decl_constraint : ID ':' type ;

func_decl_arg
	: unit
	| '[' func_decl_arg ']'
	| '(' func_decl_arg (',' func_decl_arg)* ')'
	| '(' func_decl_arg (<assoc=right> '->'func_decl_arg)+ ')'
	| '{' func_decl_arg ('->' func_decl_arg)* '}'
	| fda_id=ID
	| fda_tid+=TID ('.' fda_tid+=TID)* (fda_tid_tid_arg+=TID | fda_tid_id_arg+=ID)*
	;

unit : '(' ')' | '!' ;

func_name_decl : f_id=ID | f_op=op ;

func_def
	: 'let' (let_func_name=lid|op) (let_func_args+=let_arg)* let_expr
	| 'let' '(' lid (',' lid)* ')' '=' expr
	;

val_def
    :  'val' val_id=ID (':' type)? ('=' expr)
    | 'val' '(' lid (',' lid)* ')' '=' expr ;


lid : lid_fun_id=ID | lid_val_id=ID ':' type ;

let_arg
    : l_atom=let_arg_atom
    | '[' (let_arg_atom (',' let_arg_atom)*)? ']'
    | '(' let_arg_atom (',' let_arg_atom)* ')'
    | '(' let_arg_atom ('::' let_arg_atom)* ')'
    ;

let_arg_atom
    : l_id=lid | atom | TID (ID|let_arg)* ;

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
    : INDENT (ld+=let_decl NL*)+ (where NL*)? DEDENT
    ;

let_decl
    : func_def | var | expr ;

block : INDENT (let_decl NL*)* DEDENT ;

type : '[' type ']'
     | '(' ')'
     | '(' type (',' type)* ')'
     | '{' ID ':' type (',' ID ':' type)* '}' // structs
     | TID '{' ID ':' type (',' ID ':' type)* '}' // structs
     | '{' type  '->' type '}'
     | type '->' type (<assoc=right>'->' type)*
     | tid (t_a+=tid_args)*
     | ID
     ;

tid : t+=TID ('.' t+=TID)* ;

tid_args : tid|ID|type;


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
	| paren_expr
	| '[' (list_expr)? ']'
	| '{' map_expr? '}'
	| expr '<-' expr
	| expr '@' expr
	| '$' ID
	| function=func_name (params+=expr)+
	| qual_function=qual_func_name (params+=expr)*
	| constructor
	| set_expr
	| ref=ID
	| literal=atom
	;

constructor
    : 'new' tid '(' expr_list? ')' ;

expr_list
    : expr (',' expr)*
    ;

paren_expr
    : '(' op expr* ')'
    | '(' expr_list ')'
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
