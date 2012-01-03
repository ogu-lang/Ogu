grammar Ogu;


options {
    output = AST;
    ASTLabelType = CommonTree;
    tokenVocab=Ogu;

    }

tokens {
 
    EXPR;
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
    : expr sep
    | decl 
    | (annotation)=> annotated_decl
    ;

decl
    : def | var | val;   
    
annotated_decl
    : annotation (nl annotation)* decl;

var
    : VAR id_list 
      ( COLON type (ASSIGN expr_list)? 
      | COLON ASSIGN expr_list
      | ASSIGN expr_list
      )
      sep
    ;

val
    : VAL id_list (COLON (type)?)? (ASSIGN expr_list)? sep
    ;

def
    : DEF^ id (def_func  | def_class | def_trait | def_object | ASSIGN^ class_body sep) ;

annotation
    : id 
     ( literal 
     | LPAREN expr_list RPAREN
     | named_args
     | LPAREN named_args RPAREN
     )
    ;

expr_block
    : expr | block
    ;

def_func
    : COLON! (func_args)=> func_args func_rets? func_body? sep
    | (func_args)=> func_args func_rets?  func_body? sep
    | func_expr_args  func_body? sep
    ;

func_expr_args
    : (cond_expr_list)=>cond_expr_list
    | LPAREN! (cond_expr_list)? RPAREN!
    ;

func_args
    : (func_arg)=> func_arg (COMMA func_arg)*
    | LPAREN (func_args)? RPAREN 
    ;

func_arg
    : ((id_list COLON)=> id_list COLON^)? type
    ;

func_rets
    : ARROW (type | LPAREN id COLON RPAREN) ;

func_body
    :( ASSIGN^ opt_sep expr_block
    |func_bar_body+)
    ;

func_bar_body
    : (nl)?  BAR^ cond_expr ASSIGN expr_block 
    ;

def_class
    : COLON CLASS class_args? (class_extends)? (class_satisfies)?  (ASSIGN class_body)? sep;

class_args
    : class_arg (COMMA^ class_arg)?
    | LPAREN! (class_args)? RPAREN!
    ;

class_arg
    : id_list COLON^ type
    ;

class_extends
    : GT^ generic_type ;

class_satisfies
    : TILDE^ type_expr ;
      
class_body
    : block ;
    
block
    : LCURLY (nl)? (stat)*  RCURLY
    ;
            
def_trait
    : COLON TRAIT class_satisfies (ASSIGN trait_body)? ;

trait_body : block ;

def_object
    : COLON OBJECT class_args? (class_extends)? (class_satisfies)?  (ASSIGN class_body)? sep;

type
options { backtrack = true;}
    : basic_type (COMMA basic_type)+ ARROW type 
    | basic_type ARROW type
    | basic_type
    ;

type_expr
    : type ((BAR | AMPERSAND) type)*
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
     cond_expr ( (ASSIGN)=> ASSIGN^ expr_block | (where_clause)=>where_clause)?
    | lambda_expr
    | if_expr
    | for_expr
    | case_expr
    | let_expr
    ;

where_clause
    : ((nl)=>nl)? WHERE (nl)? where_var_decl ((semi_sep where_var_decl)=>semi_sep where_var_decl)*;

where_var_decl
    : id (COLON (type)?)? ASSIGN expr_block
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
      ( (nl)? ELSIF LPAREN expr RPAREN (nl)? expr_block )*
      (nl)? ELSE (nl)? expr_block
    ;

for_expr
    : FOR LPAREN id IN expr RPAREN expr_block ;

case_expr
    : CASE LPAREN id_list RPAREN OF (nl)?
        expr BIG_ARROW expr ((sep expr BIG_ARROW)=>sep expr BIG_ARROW expr)*
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
    : add_expr ((rel_op)=> rel_op^ add_expr)*
    ;

rel_op
    : LT | GT | LE | GE | LT_MINUS;


add_expr
    : mult_expr ((add_op)=> add_op^ mult_expr)*
    ;

add_op
    : PLUS | MINUS
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
    : PLUS | MINUS | NOT ;    

post_fix_expr
options { backtrack= true;}
    : primary (DOT method_id)*
        ( (expr)=> expr
        |call
        |
        )
    ;

call
    : LPAREN (call_args)? RPAREN
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
    : INT | FLOAT | STRING
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
    : expr_list (DOT2 (expr_list))?
    | id BAR expr_list
    | id COLON expr (COMMA id COLON expr)*
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

CASE : 'case';
CLASS : 'class';
DEF : 'def';
ELSE : 'else';
ELSIF : 'elsif';
FINALLY : 'finally';
FOR : 'for';
IF : 'if';
IN : 'in' ;
LET : 'let';

OBJECT : 'object';
OF : 'of' ;
TRAIT : 'trait';
TRY : 'try';
VAR : 'var';
VAL : 'val';
WHERE : 'where';

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
DOT : '.' ;
DOT2 : '..';
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
NOT : '!' ;
POWER : '^';
BAR : '|' ;
AMPERSAND : '&' ;
TILDE : '~' ;

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;


INT :   '0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ DOT ('0'..'9')+ EXPONENT?
    |   DOT ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ; 


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
    :  ('"'|'\'') ( ESC_SEQ | ~('\\'|'"'|'\'') )* ('"'|'\'')
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




