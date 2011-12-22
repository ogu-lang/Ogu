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

prog	: (opt_sep)  (decl)* ;


stat
@after { println($stat.tree.toStringTree()+"\n"); }
    : decl | expr sep ;

decl
@after { println($decl.tree.toStringTree()+"\n"); }
    : def | var | val;
    
var
    : VAR id_list (COLON (type)?) ASSIGN expr_list sep
    ;

val
    : VAL id_list (COLON (type)?) ASSIGN expr_list sep
    ;

def
@after { println($def.tree.toStringTree()+"\n"); }
    : DEF^ id (def_func  | def_class | def_trait | ASSIGN^ class_body sep) ;


def_func
    : COLON! (func_args)=> func_args (func_rets)? (ASSIGN^ (expr|block))? sep
    | (func_args)=> func_args (func_rets)? (ASSIGN^ (expr|block))? sep
    | func_expr_args ASSIGN^ (expr | block) sep
    ;

func_expr_args
    : (eq_expr_list)=>eq_expr_list
    | LPAREN! (eq_expr_list)? RPAREN!
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
    : COLON TRAIT class_satisfies ASSIGN (trait_body)?  ;

trait_body : block ;


type
options { backtrack = true;}
    : basic_type (COMMA basic_type)+ ARROW type 
    | basic_type ARROW type
    | basic_type
    ;

type_expr
    : type ((BIT_OR | BIT_AND) type)*
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
    :   eq_expr ((ASSIGN)=> ASSIGN^ (expr|block))?
    | lambda_expr
    ;

eq_expr_list
    : eq_expr (COMMA eq_expr)* ;

eq_expr
    : rel_expr ((eq_op)=> eq_op^ rel_expr)*
    ;

eq_op : EQ | NE ;

rel_expr
    : add_expr ((rel_op)=> rel_op^ add_expr)*
    ;

rel_op
    : LT | GT | LE | GE | IN;


add_expr
    : mult_expr ((add_op)=> add_op^ mult_expr)*
    ;

add_op
    : PLUS | MINUS
    ;

mult_expr
    : post_fix_expr ((mul_op)=> mul_op^ post_fix_expr)*
    ;

mul_op
    : MULT | DIV | CONS
    ;

unary_expr
    : unary_op post_fix_expr
    | post_fix_expr
    ;

unary_op
    : PLUS | MINUS | NOT ;    

post_fix_expr
options { backtrack= true;}
    : primary 
        ( call_args 
        | (expr)=> expr
        |
        )
    ;

primary
    : atom
    | LPAREN expr_list RPAREN ((primary)=>primary)?
    | LBRACKET (bracket_inner_expr)? RBRACKET
    ;

atom:    literal | id ;

literal
    : INT | FLOAT | STRING
    ;

call_args
    : (call_arg)=> call_arg ((COMMA)=> COMMA call_arg)*
    | LPAREN (call_args)? RPAREN
    ;

call_arg
    : (id COLON)=> id COLON expr 
    | expr;

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
    ;



// esto hay que mejorarlo, es un parche para ciertos bloques patológicos (ver test_block)
sep 
@init {
    int marker = input.mark();
}
: SEMI ! 
| (NL!)+
| RCURLY { input.rewind(marker); }
;

opt_sep : (sep)? ;

nl : (NL!)+ ;

CLASS : 'class';
DEF : 'def';
TRAIT : 'trait';
VAR : 'var';
VAL : 'val';

ASSIGN : '=';
LAMBDA : '\\';
ARROW : '->';
COLON : ':' ;
CONS : '::' ;
COMMA : ',' ;
DOT : '.' ;
DOT2 : '..';
PLUS_ASSIGN : '+=' ;
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
LT : '<';
GT : '>';
LE : '<=';
GE : '>=';
EQ : '==';
NE : '!=';
NOT : '!' ;
IN : 'in' ;
BIT_OR : '|' ;
BIT_AND : '&' ;
TILDE : '~' ;

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')+ EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ; 

INT :	'0'..'9'+
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
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;    
    
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




