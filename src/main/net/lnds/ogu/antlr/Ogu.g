grammar Ogu;


options {
    output = AST;
    ASTLabelType = CommonTree;
    tokenVocab=Ogu;
    }

tokens {
    ANNOTATION;
    EXPR;
    FUNCTION;
    INT_LITERAL;
    FLOAT_LITERAL;
    STRING_LITERAL;
}

@header {
    package net.lnds.ogu.antlr.parser;

}

@lexer::header {
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

prog	: (opt_sep)  (stat)* ;


stat
options { backtrack = true;}
@after { println($stat.tree.toStringTree()+"\n"); }
    : decl 
    | (annotated_decl)=> annotated_decl 
    | expr sep 
    ;


decl
    : def | var_val | import_decl | export_decl ;   
    
annotated_decl
    : (annotation nl)+ decl;

import_decl
    : IMPORT^ path_list sep
    ;
export_decl
    : EXPORT^ path_list sep
    ;

path_list
    : path (COMMA path)* ;

path
    : id (DOT id)*
    ;

var_val
    : (VAR|VAL)^ 
      (id_list 
          ( COLON type_expr (ASSIGN! expr_list)? 
          | COLON ASSIGN! expr_list
          | ASSIGN! expr_list
          )
      | LPAREN id_list RPAREN ASSIGN! expr_list
      )
      sep
    ;


def
    : DEF^ id (def_gen_args)? (def_module | def_func  | def_class |  def_trait | def_object | ASSIGN^ (type_expr|class_body) sep) ;

annotation
    : i=id 
     ( l=literal -> ^(ANNOTATION $i $l)
     | LPAREN expr_list RPAREN -> ^(ANNOTATION $i expr_list)
     | named_args -> ^(ANNOTATION $i named_args)
     | LPAREN named_args RPAREN -> ^(ANNOTATION $i named_args)
     | -> ^(ANNOTATION $i)
     )
     
    ;

def_gen_args
    : LCURLY type_constraint  RCURLY
    ;

type_constraint
    : id_list 
      (BAR type_constraint_expr (COMMA type_constraint_expr)*)?
    ;

type_constraint_expr
    : id (GT|TILDE) type_expr
    ;


expr_block
    : expr | block
    ;

block
    : LCURLY (nl)? (block_stat)*  RCURLY
    ;

arg_block
    : LCURLY (nl)? 
        (id_list ARROW (nl)?)?
        (block_stat)*  
        RCURLY
    ;

block_stat
    : decl 
    | expr sep
    ;

def_func
    : COLON (func_args)=> fa=func_args ((func_rets)=> fr=func_rets)? fb=func_body? sep ->^(FUNCTION $fa ($fr)? $fb?)
    | (func_args)=> fa=func_args fr=func_rets?  fb=func_body? sep ->^(FUNCTION $fa ($fr)? $fb?)
    | fea=func_expr_args  fb=func_body sep ->^(FUNCTION $fea  $fb?)
    ;


func_expr_args
    : (cond_expr_list)=>cond_expr_list
    | LPAREN! (cond_expr_list)? RPAREN!
    ;

func_args
    : (func_arg_list)=> func_arg_list
    | LPAREN (func_arg_list)? RPAREN 
    ;

func_arg_list
    : func_arg (COMMA func_arg)*
    ;


func_arg
    : ((id_list COLON)=> id_list COLON^)? type_expr (MULT|PLUS)?
    ;


func_rets
    : ARROW (type_expr | LPAREN id COLON type_expr RPAREN) ;

func_body
    : (func_require)? (func_ensure)?
    
    ( ASSIGN^ opt_sep expr_block
     | (func_bar_body)=>func_bar_body+ (otherwise)?
     )
    (where_clause)?
    ;

func_require
    : (nl)? REQUIRE expr_block;

func_ensure
    : (nl)? ENSURE expr_block;


func_bar_body
    : nl  BAR^ cond_expr ASSIGN expr_block 
    ;
otherwise
    : nl BAR^ OTHERWISE ASSIGN expr_block
    ;

where_clause
    : ((nl)=>nl)? WHERE^ (nl)? 
        (where_decls | LCURLY where_decls RCURLY)
    ;

where_decls:        
        where_var_decl ((semi_sep where_var_decl)=>semi_sep where_var_decl)*;

where_var_decl
    : id 
      (COLON (type)?
      |(arg_list)=> arg_list
      |(cond_expr_list)=> cond_expr_list
      |)
      ASSIGN expr_block
    ;

arg_list
    : id_list
    | id_list COLON type (COMMA id_list COLON type)*
    ;

def_module
    : COLON! MODULE^ (nl)? ASSIGN expr_block sep ;


def_class
    : COLON! CLASS^ class_args? (class_extends)? (class_satisfies)?  ((nl)? ASSIGN class_body)? sep;

class_args
    : class_arg (COMMA^ class_arg)?
    | LPAREN! (class_args)? RPAREN!
    ;

class_arg
    : id_list COLON^ type
    ;

class_extends
    : (nl)? (GT|EXTENDS)^ generic_type ;

class_satisfies
    :  (nl)? (TILDE|SATISFIES)^ type_expr ;
      
class_body
    : class_block ;
    

class_block
    :  LCURLY (nl)? (stat)*  RCURLY
    ;
      

            
def_trait
    : COLON! TRAIT^ class_satisfies (ASSIGN trait_body)? ;

trait_body : block ;

def_object
    : COLON! OBJECT^ class_args? (class_extends)? (class_satisfies)?  (ASSIGN class_body)? sep;

type
options { backtrack = true;}
    : basic_type (COMMA basic_type)* ARROW type 
    | basic_type 
    ;

type_expr
    : type (((BAR)=>BAR | AMPERSAND) type)*

    ;

type_expr_list
    : type_expr (COMMA type_expr) 
    ;


generic_type : id (DOT id)* (LCURLY type_list RCURLY)?  ;

type_list    : type (COMMA type)*    ;

bracket_type : LBRACKET type (COLON type)? RBRACKET;

tuple_type : LPAREN type_list RPAREN;

basic_type : (generic_type | bracket_type | tuple_type) ((array_def)=> array_def)? ;

basic_type_list : basic_type (COMMA basic_type)* ;

array_def
    : LBRACKET INT (COMMA INT)* RBRACKET ;

id : ID ;

id_list : id (COMMA id)* ;

expr_list
    : expr (COMMA expr)*
    ;

expr 
    :  
     cond_expr ( (ASSIGN)=> ASSIGN^ expr_block )?
    | lambda_expr
    | if_expr
    | for_expr
    | case_expr
    | let_expr
    | do_expr
    | try_expr
    | fail_expr
    | retry_expr
    | yield_expr
    | return_expr
    | assert_expr
    | while_expr
    ;

assert_expr
    : ASSERT expr
    ;

return_expr
    : RETURN expr
    ;

let_expr
    : LET (nl)? let_var_decl ((semi_sep let_var_decl)=> semi_sep let_var_decl)* 
      (nl)?
      IN (nl)? expr_block
    ;

let_var_decl
    : id (COLON (type)?)? ASSIGN expr_block 
    ;

if_expr
    : IF LPAREN expr RPAREN (nl)? expr_block
      (elsif_part)*
      else_part
      
    ;

elsif_part
    : (nl)? ELSIF LPAREN expr RPAREN (nl)? expr_block 
    ;
else_part
    : (nl)? ELSE (nl)? expr_block ;

for_expr
    : FOR LPAREN id (IN|LT_MINUS) expr RPAREN (nl)? expr_block ;

do_expr
    : DO expr_block
    ;

yield_expr
    : YIELD expr_block
    ;

while_expr
    : WHILE LPAREN cond_expr RPAREN (nl)? expr_block ;

fail_expr
    : FAIL 
    ;

retry_expr
    : RETRY
    ;


try_expr
    : TRY (nl)? expr_block RESCUE expr_block
    ;

case_expr
    : CASE LPAREN id_list RPAREN OF (nl)?
        expr ARROW expr ((sep expr ARROW)=>sep expr ARROW expr)*
        ;

cond_expr_list
    : cond_expr (COMMA cond_expr)* ;

cond_expr
    : and_expr ((OR)=> OR and_expr)* ;

and_expr
    : eq_expr ((AND)=> AND eq_expr)* ;

eq_expr
    : rel_expr ((eq_op)=> eq_op^ rel_expr)*
    ;

eq_op : EQ | NE ;

rel_expr
    : in_expr ((rel_op)=> rel_op^ in_expr)*
    ;

rel_op
    : LT | GT | LE | GE ;

in_expr
    : is_expr ((in_op)=> in_op is_expr)*
    ;

in_op
    : LT_MINUS | ((NOT)=> NOT)? IN ;

is_expr
    : add_expr ((is_op)=> is_op^  add_expr)* ;

is_op
    : ((NOT)=> NOT)? IS ;

add_expr
    : mult_expr ((add_op)=> add_op^ mult_expr)*
    ;

add_op
    : PLUS | MINUS | PLUSPLUS
    ;

mult_expr
    : power_expr ((mul_op)=> mul_op^ power_expr)*
    ;

mul_op
    : MULT | DIV | CONS | MOD
    ;

power_expr
    : unary_expr ((POWER)=>POWER^ unary_expr)*
    ;

unary_expr
    : (unary_op )=> unary_op unary_expr
    | post_fix_expr
    ;

unary_op
    : PLUS | MINUS | NOT | BANG;    

post_fix_expr
options { backtrack= true;}
    : primary (DOT method_id)*
        ( (expr)=> expr
        | call 
        | AS type
        | arg_block
        |
        )
    ;

method_call
    : (DOT method_id)+ (call)?
    ;

call
    : LPAREN (call_args)? RPAREN (method_call)?
    | (named_args)=> named_args
    ;

primary
    : atom
    | LPAREN expr_list RPAREN ((primary)=>primary)?
    | LBRACKET (bracket_inner_expr)? RBRACKET
    ;

atom:    literal | id ;

method_id
    : id | PLUS | MINUS | MULT | DIV | MOD
    ;

literal
    : i=INT->^(INT_LITERAL $i) 
    | f=FLOAT->^(FLOAT_LITERAL $f) 
    | s=STRING->^(STRING_LITERAL $s)
    | SELF
    | SUPER
    | NIL
    ;

call_args
    : call_arg ((COMMA)=> COMMA call_arg)*
    ;

call_arg
    : (named_arg)=> named_arg
    | expr;

named_arg
    : id COLON expr
    ;

named_args
    : named_arg ((COMMA)=> COMMA named_arg)*
    ;

lambda_expr
    : LAMBDA lambda_args ARROW expr
    ;

lambda_args
    : lambda_arg (COMMA lambda_arg)*
    | LPAREN lambda_args RPAREN
    ;

lambda_arg
    : id_list COLON type
    ;

bracket_inner_expr
    : expr_list (DOT2^ (expr_list))? (BAR expr_list)?
    | (id COLON)=>id COLON expr (COMMA id COLON expr)*
    
    ;



// esto hay que mejorarlo, es un parche para ciertos bloques patológicos (ver test_block)
sep 
@init {
    int marker = input.mark();
}
: SEMI! (NL!)* 
| (NL!)+
| RCURLY { input.rewind(marker); }
| EOF!
;

semi_sep
    : SEMI! | (NL!)+
    ;

opt_sep : (sep)? ;

nl : (NL!)+ ;

AS : 'as';
ASSERT : 'assert';
CASE : 'case';
CLASS : 'class';
DEF : 'def';
DO : 'do';
ELSE : 'else';
ELSIF : 'elsif';
ENSURE : 'ensure';
EXPORT: 'export';
EXTENDS: 'extends';
FAIL : 'fail';
FOR : 'for';
IF : 'if';
IMPORT : 'import';
IN : 'in' ;
IS : 'is';
LET : 'let';
MODULE : 'module' ;
NIL : 'nil';
NOT : 'not';

OBJECT : 'object';
OF : 'of' ;
OTHERWISE : 'otherwise' ;
REQUIRE : 'require';
RESCUE : 'rescue';
RETRY : 'retry';
RETURN : 'return';
SATISFIES : 'satisfies';
SELF : 'self';
SUPER : 'super';

THROW : 'throw';
TRAIT : 'trait';
TRY : 'try';

VAR : 'var';
VAL : 'val';
WHERE : 'where';
WHILE : 'while';
YIELD : 'yield';

AND : '&&' ;
OR  : '||' ;

ARROBA : '@';
ASSIGN : '=';
LAMBDA : '\\';
ARROW : '->';
BIG_ARROW : '=>';

COLON : ':' ;
CONS : '::' ;
COMMA : ',' ;

PLUSPLUS : '++';
PLUS : '+' ;
MINUS : '-';
SEMI : ';' ;
LPAREN : '(' { ++parenLevel; }; 
RPAREN : ')' { --parenLevel; };
LCURLY : '{' { ++curlyLevel; };
RCURLY : '}' { --curlyLevel; };
LBRACKET : '[' { ++bracketLevel; };
RBRACKET : ']' { --bracketLevel; };
MULT : '*' ;
DIV : '/' ;
MOD : '%' ;
LT : '<';

LT_MINUS : '<-';
GT : '>';
LE : '<=';
GE : '>=';
EQ : '==';
NE : '!=';
BANG : '!' ;
POWER : '^';
BAR : '|' ;
AMPERSAND : '&' ;
TILDE : '~' ;

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;


fragment INT :  ; //:   '0'..'9'+
    

fragment DOT2 : ;
fragment DOT : ;


FLOAT
    : Digits ( 
        { input.LA(2) != '.'}?=> '.' Digits EXPONENT?  { $type = FLOAT; }
        | EXPONENT { $type= FLOAT; }
        | {$type=INT;}
        )
    | '.' 
       (Digits EXPONENT? {$type = FLOAT; } 
       | '.' {$type = DOT2; } 
       | {$type=DOT;} 
       )
    
    ;

fragment Digits : ('0'..'9')+;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN;}
    ;

  
NL : ('\r\n'| '\r'     | '\n') { if (ignoreNewLines()) $channel=HIDDEN; }
    ;
WS  :   ( ' '
        | '\t'
        | '\f'
        ) { $channel=HIDDEN;}
    ;
    
    
STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"' ))* '"'
    |  '\'' ( ESC_SEQ | ~('\\'|'\'' ))* '\''
    ;
    
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




