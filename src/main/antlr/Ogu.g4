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
    : decs=decorators?
    ( var | val_def | func_decl | func_def | alias_def | trait_def | instance_def | type_def | data_def | enum_def | class_def )
    | expr
    ;

decorators :  (dec+=decorator)+ ;

decorator : '#' '{' dec_id=ID (dec_args+=STRING)* '}' NL*;

alias_def : 'alias' alias_target '=' alias_origin NL*;

alias_target : alias_tid=TID | alias_id=ID;

alias_origin :  alias_origin_tid+=TID ('.' alias_origin_tid+=TID)* ('.' alias_origin_id=ID)? ;

enum_def : 'enum' en=TID '=' values+=ID ('|' values+=ID)* deriving? ;

data_def : 'data' constraints=typedef_args_constraints? name=TID (typedef_params)? '=' data_type_decl ;

trait_def : (mut='mut')? 'trait' constraints=typedef_args_constraints? name=TID params+=ID (params+=ID)* ('where' INDENT (internal_decl NL* )* NL* DEDENT)?   ;

instance_def : 'instance' constraints=typedef_args_constraints? name=TID params+=type params+=type* ('where' INDENT (internal_decl NL* )* NL* DEDENT)? ;

internal_decl : decorators? (func_decl|func_def|var|val_def) ;

class_def : 'mut'? 'class' constraints=typedef_args_constraints? TID ID* '(' class_params? ')' ('=' class_ctor | class_where)? ;

class_params : class_param (',' class_param)* ;

class_param : ('var'|'val')? ID (',' ID)* ':' type ;

class_ctor : TID '(' expr_list? ')'  ;

class_where : 'where' INDENT (internal_decl NL* )* NL* DEDENT ;

type_def :  'type' constraints=typedef_args_constraints? t=TID (ta=typedef_params)? ('=' type)?  ;

data_type_decl : t+=type ('|' t+=type)* deriving?
              | t+=type  INDENT ('|' t+=type NL*)*  deriving? NL* DEDENT
              ;

deriving : 'deriving' deriving_types | INDENT 'deriving' deriving_types NL* DEDENT ;

deriving_types :  '(' dt+=tid (',' dt+=tid)* ')' ;

typedef_params
	:  params+=ID (params+=ID)*
	;

typedef_args_constraints
	: '(' tac+=typedef_arg_constraint (',' tac+=typedef_arg_constraint)* ')' '=>'
	;

typedef_arg_constraint
	: ids+=ID (',' ids+=ID)* ':' t=type
	;

enum_values : ID ((NL)* '|' ID)* ;

func_decl :
    'def' name=func_name_decl ':' constraints=typedef_args_constraints?
      (arg+=func_decl_arg (<assoc=right> '->' arg+=func_decl_arg)*
      | '->'? func_decl_arg
      );

var :  'var' vid (':' type)? ('=' expr)? ;

vid : ID | '(' vidt (',' vidt)* ')' ;

vidt : ID (':' type)? ;

func_decl_arg
	: unit
	| divergence
	| '[' func_decl_arg ']'
	| '(' func_decl_arg (',' func_decl_arg)* ')'
	| '(' func_decl_arg (<assoc=right> '->'func_decl_arg)+ ')'
	| '{' func_decl_arg ('->' func_decl_arg)* '}'
	| fda_id=ID
	| fda_tid+=TID ('.' fda_tid+=TID)* (fda_tid_tid_arg+=TID | fda_tid_id_arg+=ID)*
	;

unit : '(' ')' ;

divergence : '!' ;

func_name_decl : f_id=ID | f_op=op | '(' f_op=op ')';

func_def
	: 'let'
	( (let_func_name=lid|op) (let_func_args+=let_arg)* let_expr
	| left=let_arg infix_op=op right=let_arg let_expr
	| let_arg '`' infix_id=ID '`'  let_arg let_expr
    | '(' lid (',' lid)* ')' '=' expr
	)
	;

val_def
    :  'val' val_id=ID (':' type)? ('=' expr)
    | 'val' '(' lid (',' lid)* ')' '=' expr
    ;


lid : lid_fun_id=ID | lid_val_id=ID ':' type ;

let_arg
    : l_atom=let_arg_atom
    | '[' (let_arg_atom (',' let_arg_atom)*)? ']'
    | '(' let_arg_atom (',' let_arg_atom)* ')'
    | '(' let_arg_atom ('::' let_arg_atom)* ')'
    ;

let_arg_atom
    : l_id=lid | atom | t_id=TID la=let_arg* ;

let_expr : '=' ( let_block | expr let_where? )
         |  guards where?
         ;

guards : INDENT (guard NL*)*   (where NL*)? DEDENT;

guard : '|' expr (expr)* '=' (expr|let_block) NL*  ;

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

type : vector_type
     | unit
     | tuple_type
     | '{' ID ':' type (',' ID ':' type)* '}' // structs
     | TID '{' ID ':' type (',' ID ':' type)* '}' // structs
     | '{' type  '->' type '}'
     | type '->' type (<assoc=right>'->' type)*
     | tid (t_a+=tid_args)*
     | nat=('i8' | 'u8' | 'i16' | 'u16' | 'i32' | 'u32' | 'i64' | 'u64' | 'f32' | 'f64')
     | ID
     ;

vector_type : '[' vt=type ']' ;

tuple_type : '(' t+=type (',' t+=type)* ')';

tid : t+=TID ('.' t+=TID)* ;

tid_args : tid|ID|type;

expr  
	: if_expr
	| 'for' (set_expr | list_expr) do_expression
    | case_expr
	| 'when' expr  do_expression
	| 'while' expr do_expression
	| 'unless' expr  do_expression
	| 'let' ID '=' expr (',' ID '=' expr)* ('in' expr)?
	| '\\' lambda_args? '->' (expr|block)
	| 'yield' expr
	| 'recur' expr*
	| expr '<-' expr
	|<assoc=right> l=expr o=('>>'|'<<'|'|>'|'<|') r=expr  // composition
    | l=expr o='@' r=expr
	|<assoc=right> l=expr o='^' r=expr
	| l=expr o=('*'|'/'|'//'|'%') r=expr
	| l=expr o=('+'|'-') r=expr
    | <assoc=right> l=expr o='::' r=expr
    | <assoc=right> l=expr o='++' r=expr
    | expr '..' (expr)?
	| expr '...'
    | l=expr o=('=='|'/='|'<'|'<='|'>'|'>=') r=expr
    | l=expr o='&&' r=expr
    | l=expr o='||' r=expr
	| paren_expr
	| vector_expr
	| dict_expr
	| expr '`' ID '`' expr
	| '$' ID
	| function=func_name (params+=expr)+
	| qual_function=qual_func_name (params+=expr)*
	| constructor
	| set_expr
	| ref=ID
	| primary
	;

primary
	    : neg_expr
	    | literal=atom
	    ;

atom : i=INT | f=FLOAT | string_literal=STRING | c=CHAR | d=DATE ;

neg_expr
    : '(' '-' e=expr ')'
    | '-' (a=atom);

constructor
    : 'new' tid '(' expr_list? ')' ;

expr_list
    : e+=expr (',' e+=expr)*
    ;

paren_expr
    : '(' op expr* ')'
    | '(' expr_list ')'
    ;

vector_expr
    : '[' (list_expr)? ']' ;

dict_expr
    : '{' map_expr? '}' ;


func_name : name=ID;

qual_func_name : qual+=TID ('.' qual+=TID)* ('.' name=ID)? ;

if_expr : 'if' cond=expr then_part ;

then_part : 'then' (te=expr  else_part|te=expr? tb=then_block else_part) | INDENT 'then' (te=expr NL* else_part |te=expr? tb=then_block else_part) NL* DEDENT ;

else_part : 'else' (e=expr|e=expr? eb=else_block) ;

then_block : INDENT (let_decl NL*)+ NL* DEDENT ;

else_block : INDENT (let_decl NL*)+ DEDENT ;

case_expr : 'case' s=expr 'of' g=case_guards ;

case_guards : INDENT cg+=case_guard NL* (cg+=case_guard NL*)* DEDENT ;

case_guard : c=expr '=>' r=expr ;

op : '@' | '^' | '*' | '/' | '%' |  '//' | '+' | '-'  | '++' | '::'
	| '~' | '..' | '...' | '|>' | '<|'
	| '==' | '/='
	| '>' | '<'  | '>=' | '<='
	| '|' | '||' | '&&'
	;

map_expr
    : expr '->' expr (',' expr '->' expr)*
    | ID '=' expr (',' ID '=' expr)*
    ;


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

// A _A _Ab-> TID
// _ a _a _aB -> ID


ID : '_' | '_'?[a-záéíóúñ][_a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ('!'|'?')?;

TID :'_'? [A-ZÁÉÍÓÚÑ][_a-záéíóúñA-ZÁÉÍÓÚÑ0-9]* ('\'')* ;

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
