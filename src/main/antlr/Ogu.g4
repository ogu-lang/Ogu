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

module_body : (members+=module_decl NL*)* (expr NL*)* ;

module_decl
    : decs=decorators?
    ( var | val_def | func_decl | func_def | alias_def | trait_def | instance_def | type_def | data_def | enum_def | class_def )
    | expr
    ;

decorators :  (dec+=decorator)+ ;

decorator : '#' '{' dec_id=ID (dec_args+=STRING)* '}' NL*;

alias_def : 'alias' alias_target '=' alias_origin NL*;

alias_target : alias_tid=TID | alias_id=ID;

alias_origin : jvm_id=jvm_origin
             | alias_origin_tid+=TID ('.' alias_origin_tid+=TID)* ('.' alias_origin_id=ID)? ;

jvm_origin : 'jvm' src=STRING ;

enum_def : 'enum' en=TID '=' values+=ID ('|' values+=ID)* deriving? ;

data_def : 'data' constraints=typedef_args_constraints? name=TID (typedef_params)? '=' data_type_decl ;

trait_def : (mut='mut')? 'trait' constraints=typedef_args_constraints? name=TID params+=ID (params+=ID)* ('where' INDENT (internal_decl NL* )* NL* DEDENT)?   ;

instance_def : 'instance' constraints=typedef_args_constraints? name=TID params+=type params+=type* ('where' INDENT (internal_decl NL* )* NL* DEDENT)? ;

internal_decl : decorators? (func_decl|func_def|var|val_def) ;

class_def : (mut='mut')? 'class' constraints=typedef_args_constraints? name=TID params+=ID* '(' class_params? ')' ('=' class_ctor | 'where' INDENT (internal_decl NL* )* NL* DEDENT )? ;

class_params : class_param (',' class_param)* ;

class_param : ('var'|'val')? ID (',' ID)* ':' type ;

class_ctor : TID '(' tuple_expr? ')'  ;

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

var :  'var' vid=ID (':' type)? ('=' expr)?
    |  'var'  '(' vidt (',' vidt)* ')' ('=' expr)? ;

vidt : i=ID (':' t=type)? ;

func_decl_arg
	: divergence
	| vector
    | unit
	| tuple
	| '(' func_decl_arg (<assoc=right> '->'func_decl_arg)+ ')'
	| '{' func_decl_arg ('->' func_decl_arg)* '}'
	| fda_id=ID
	| fda_or1=func_decl_arg '|' fda_or2=func_decl_arg (<assoc=right>'|' fda_orn+=func_decl_arg)*
	| fda_tid+=TID ('.' fda_tid+=TID)* (tid_or_id_arg+=tid_or_id)*
	;

tid_or_id
	: t=TID | i=ID ;

unit : '(' ')' ;

divergence : '!' ;

vector : '[' func_decl_arg ']' ;

tuple : '(' func_decl_arg (',' func_decl_arg)* ')' ;

type : vector_type
     | unit
     | tuple_type
     | anon_record_type
     | record_type
     | map_type
     | type '->' type (<assoc=right>'->' type)*
     | gt=tid (t_a+=tid_args)*
     | type '|' type (<assoc=right>'|' type)*
     | nat=('b8' | 'c8' | 'c16' | 'c32' | 'i8' | 'u8' | 'i16' | 'u16' | 'i32' | 'u32' | 'i64' | 'u64' | 'f32' | 'f64')
     | i=ID
     ;

func_name_decl : f_id=ID | f_op=op | '(' f_op=op ')';

func_def
	: 'let'
	( let_func_name=lid (let_func_args+=let_arg)* let_expr
	| prefix_op=op left=let_arg right=let_arg let_expr
	| left=let_arg infix_op=op right=let_arg let_expr
	| let_arg '`' infix_id=ID '`'  let_arg let_expr
    | '(' tup+=lid (',' tup+=lid)* ')' '=' expr
	)
	;

val_def
    :  'val' val_id=ID (':' type)? ('=' expr)
    | 'val' '(' lid (',' lid)* ')' '=' expr
    ;


lid : lid_fun_id=ID | lid_val_id=ID ':' t=type ;

let_arg
    : l_atom=let_arg_atom
    | let_arg_vector
    | let_arg_tuple_or_list
    ;

let_arg_atom
    : l_id=lid | a=atom | t_id=TID (la+=let_arg)* ;

let_arg_vector
    : '[' (la+=let_arg_atom (',' la+=let_arg_atom)*)? ']'  ;

let_arg_tuple_or_list
 : '(' ta+=let_arg_atom (',' ta+=let_arg_atom)* ')'
 |  '(' la+=let_arg_atom ('::' la+=let_arg_atom)* ')' ;

let_expr : '=' ( let_block | expr let_where? )
         |  guards where?
         ;

guards : INDENT (guard NL*)*   (where NL*)? DEDENT;

guard : '|' be=expr (ae+=expr)* '=' (de=expr|eb=let_block) NL*  ;

let_where : INDENT where NL* DEDENT ;

where : 'where' wl+=where_expr? (INDENT (wl+=where_expr NL*)+ DEDENT)? ;

where_expr
	: i=ID let_arg* NL* let_expr
	| '(' tup+=lid (',' tup+=lid)* ')' '=' expr
	;


let_block
    : INDENT (ld+=let_decl NL*)+ (where NL*)? DEDENT
    ;

let_decl
    : func_def | val_def | var | expr ;

block : INDENT (let_decl NL*)* DEDENT ;



vector_type : '[' vt=type ']' ;

tuple_type : '(' t+=type (',' t+=type)* ')';

map_type : '{' k=type  '->' v=type '}' ;

tid : t+=TID ('.' t+=TID)* ;

tid_args : tid|i=ID|type;

anon_record_type : '{' fldDecl (',' fldDecl)* '}' ; // anonymous structs

record_type : ti=TID '{' fldDecl (',' fldDecl)* '}' ; // structs

fldDecl : i=ID ':' t=type ;

expr  
	: if_expr
	| for_expr
    | case_expr
    | when_expr
    | while_expr
	| 'unless' expr  do_expression
	| let_in_expr
	| lambda_expr
	| yield_expr
	| recur_expr
    | function=func_name params=params_expr
	| qual_function=qual_func_name params=params_expr
	| assign_expr
	|<assoc=right> l=expr o=('>>'|'<<'|'|>'|'<|') r=expr  // composition
    | l=expr o='@' r=expr
	|<assoc=right> l=expr o='^' r=expr
	| l=expr o=('*'|'/'|'//'|'%') r=expr
	| l=expr o=('+'|'-') r=expr
    | <assoc=right> l=expr o='::' r=expr
    | <assoc=right> l=expr o='++' r=expr
    | l=expr o=('=='|'/='|'<'|'<='|'>'|'>=') r=expr
    | l=expr o='&&' r=expr
    | l=expr o='||' r=expr
	| l_infix=expr '`' infix_id=ID '`' r_infix=expr
	| self_id
	| ref=ID
	| primary
	| paren_expr
    | vector_expr
    | dict_expr
	;

params_expr
    : param_expr+
    ;

param_expr
    : self_id
    | ref=ID
    | primary
    | paren_expr
    | expr
    ;

self_id
    : '$' i=ID ;

let_in_expr
    : 'let' let_in_arg (',' let_in_arg)* ('in' in_expr=expr)?  ;

let_in_arg
    : i=ID '=' e=expr ;

recur_expr
    : 'recur' expr*  ;

yield_expr
    : 'yield' expr ;


lambda_expr
    	: '\\' lambda_args? '->' (expr|block)  ;

when_expr : 'when' expr do_expression ;

while_expr : 'while' expr do_expression ;

primary
	    : neg_expr
	    | literal=atom
	    ;

atom : i=INT | f=FLOAT | string_literal=STRING | c=CHAR | d=DATE ;

neg_expr
    : '(' '-' e=expr ')'
    | '-' (a=atom);


tuple_expr
    : e+=expr (',' e+=expr)*
    ;

paren_expr
    : '(' op expr* ')'
    | '(' tuple_expr ')'
    ;

assign_expr
	: i=ID a=expr? '<-' e=expr
	| '$' si=ID a=expr? '<-' e=expr
	| 'set' i=ID a=expr? '=' e=expr
    ;

vector_expr
    : '[' (list_expr)? ']' ;


// [1,2,3]
// [(a,b) | a <- [0..10], b <- ['A', 'B']]
// [1..10, 20..30]
// [1...]
// [1..10, 20..30, 40...]
//[2 * x | x <- 1...]
list_expr : le+=range_expr (',' le+=range_expr)*
          | e=expr '|' se+=set_constraint_expr (',' se+=set_constraint_expr)* ;

set_constraint_expr : s_id=ID '<-' re=expr
         | '(' l_id+=ID (',' l_id+=ID)* ')' '<-' re=expr
        ;

range_expr :  beg=expr ('..' end=expr)?
           | beg=expr '...'
           ;

dict_expr
    : '{' map_expr? '}' ;


func_name : name=ID;

qual_func_name : qual+=TID ('.' qual+=TID)* ('.' name=ID)? ;

if_expr : 'if' cond=expr then_part ;

then_part : 'then' (te=expr  else_part|te=expr? tb=then_block else_part) | INDENT 'then' (te=expr NL* else_part |te=expr? tb=then_block else_part) NL* DEDENT ;

else_part : 'else' (e=expr|e=expr? eb=else_block) ;

then_block : INDENT (let_decl NL*)+ NL* DEDENT ;

else_block : INDENT (let_decl NL*)+ DEDENT ;

for_expr : 'for' set_constraint_expr  do_expression ;

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
    : ma+=m_arrow (',' ma+=m_arrow)*
    | mb+=m_assign (',' mb+=m_assign)*
    ;

m_arrow : k=expr '->' v=expr ;

m_assign : i=ID '=' e=expr ;

lambda_args
	: lambda_arg lambda_arg*
	;

lambda_arg
	: i=ID (':' type)?
	| '(' (la+=lambda_arg (',' la+=lambda_arg)*)? ')'
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
