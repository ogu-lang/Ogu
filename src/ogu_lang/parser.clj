(ns ogu-lang.parser
  (:require
    [clojure.string :as string]
    [instaparse.core :as insta])
  (:use clojure.java.io)
  (:use ogu.core))

(def grammar
  (insta/parser
    "module = [module-header] {uses} {NL} {definition / type-def NL / trait-def / val-def NL / var-def NL / set-var NL / module-expr}

     module-header = <'module'> BS+ module-name BS* NL

     uses = <'uses'> BS* module-name BS* {<\",\"> BS* module-name BS*} NL

     module-name = TID {\".\" TID} | 'jvm' BS+ (ID|TID) {\".\" (ID|TID)}

     definition = &\"def\" <\"def\"> BS+ ID  def-args (def-body) [where]

     def-args = {BS+ arg} [\"&\" BS+ arg]

     <arg> = &isa-type isa-type / &type-pattern type-pattern / &ID ID /  func-call-expr

     type-def = <'type'> BS+  type-constructor-def [BS* <\":\"> BS* type-constructor-def] {BS* <\":\"> BS* TID} BS*

     type-constructor-def = TID BS* <\"(\"> BS* id-list BS* <\")\">

     id-list = ID BS* {<\",\"> BS* ID}

     trait-def = <'trait'> BS+ TID BS* 'is' BS* NL trait-methods   BS* <'end'>
     trait-methods = trait-method {trait-method}
     trait-method = BS* ID {BS+ trait-method-arg} BS* NL
     trait-method-arg = TID | ID

     val-def = BS* <'val'> BS+ (tuple-of-ids/ID) BS+ <\"=\"> BS+ pipe-expr {BS* \",\" BS* (tuple-of-ids/ID) BS+ <\"=\"> BS+ pipe-expr}

     var-def = BS* <'var'> BS+ (tuple-of-ids/ID) BS+ <\"=\"> BS+ pipe-expr {BS* \",\" BS* (tuple-of-ids/ID) BS+ <\"=\"> BS+ pipe-expr}

     set-var = BS* <'set'> BS+ ID BS+ <\"=\"> BS+ pipe-expr

     tuple-of-ids = <\"(\"> BS* ID {BS* <\",\"> BS+ ID} BS* <\")\">

     <module-exprs> = {module-expr}

     <module-expr> = BS* pipe-expr BS* NL


     <def-body> = body-simple / NL guard+ [otherwise]
     <body-simple> = BS* <\"=\"> BS* [NL] BS* value BS* NL
     guard = BS* <\"|\"> !'otherwise' {BS+ arg} BS+ <\"=\"> BS+ value BS* NL
     otherwise = BS* <\"|\"> BS+ <'otherwise'> BS+ <\"=\"> BS+ value BS* NL

     where = BS+ <'where'> BS* [NL] where-equation {BS* <\",\"> BS* [NL BS*] where-equation} NL

     <where-equation> =  (equation | where-guards)

     equation = ID BS+ {arg BS+} <\"=\"> BS+ value

     where-guards = ID BS+ {arg} BS* [NL BS*] where-guard {NL where-guard} [NL where-otherwise]

     where-guard = BS* <\"|\"> !'otherwise' {BS+ arg} BS* <\"=\"> BS+ value
     where-otherwise = BS* <\"|\"> BS+ <'otherwise'> BS+ <\"=\">  BS+ value

     <value> = lazy-value / eager-value
     lazy-value = &<'lazy'> <'lazy'> BS+ pipe-expr
     <eager-value> = [<'eager'> BS+] pipe-expr

     <pipe-expr> =  piped-expr / func-call-expr

     <piped-expr> = forward-piped-expr  / forward-bang-piped-expr / backward-piped-expr / backward-bang-piped-expr / dollar-expr

     forward-piped-expr = func-call-expr ([NL] BS+ <\"|>\"> BS+ func-call-expr)+
     forward-bang-piped-expr = func-call-expr ([NL] BS+ <\"!>\"> BS+ func-call-expr)+
     backward-piped-expr = func-call-expr ([NL] BS+ \"<|\" BS+ func-call-expr)+
     backward-bang-piped-expr = func-call-expr ([NL] BS+ \"<!\" BS+ func-call-expr)+
     dollar-expr = func-call-expr (BS+ <\"$\"> BS+ func-call-expr)+

     <func-call-expr> = &control-expr control-expr / !control-expr lcons-expr

     recur = &'recur' <'recur'> BS+ {arg}
     return = &'return' <'return'> BS+ {arg}

     <control-expr> =  when-expr / if-expr / loop-expr  / block-expr / for-expr

     <block-expr> =  let-expr /  repeat-expr / do-expr / val-def / var-def / set-var

     <lcons-expr> =  cons-expr  /  bin-expr

     loop-expr = &'loop' <'loop'> [BS+ loop-vars-in] loop-body
     <loop-vars-in> = loop-var { BS* <\",\"> BS* [NL BS*] loop-var} BS* [NL BS*]  <'in'>
     loop-body = [NL BS*] pipe-expr &NL
     loop-var =  ID BS+ <\"=\"> BS+ loop-var-value
     loop-var-value = pipe-expr

     for-expr = for-header  <'in'> for-body
     for-header = &<'for'> <'for'> BS+ ID BS+ <\"<-\"> BS+ pipe-expr BS* [NL BS*]
     for-body = BS* [NL BS*]  pipe-expr

     let-expr = &'let' <'let'> (BS+|NL BS*) let-vars
     <let-vars> = let-vars-in  BS* [NL BS*]  let-body
     <let-vars-in> = let-var {BS* <\",\"> BS* [NL BS*] let-var} BS* [NL BS*] <'in'>
     let-var = ID BS+ <\"=\"> BS+ let-var-value | <\"(\"> BS* ID {BS* <\",\"> BS* ID} BS* <\")\"> BS+ <\"=\"> BS+ let-var-value
     let-var-value = pipe-expr
     let-body = pipe-expr &NL


     if-expr = &'if' <'if'> BS+ if-cond-expr BS* [NL BS*]  <'then'>  ([NL BS*]|BS+) then-expr [NL BS*] <'else'> ([NL BS*]|BS+) else-expr
     then-expr = pipe-expr BS*
     else-expr = pipe-expr BS*
     if-cond-expr = func-call-expr

     when-expr = &'when' <'when'> BS+ if-cond-expr BS* [NL BS*] <'then'> ([NL BS*]|BS+) then-expr &NL

     repeat-expr = &'repeat' <'repeat'> BS+ [repeat-var BS* {[NL] BS* <\",\"> BS* [NL BS*] repeat-var} ] &(NL|<'end'>)
     repeat-var  = [quoted-var BS+ <\"=\"> BS+] loop-var-value
     quoted-var = ID <#'\\''>

     <range-expr> = <\"[\"> BS* [ range-def | list-comprehension ] BS* <\"]\">
     <range-def> = range-step / range-simple / range-infinite
     range-step = NUMBER BS* <\",\"> BS* NUMBER BS* <\"..\"> BS* [\"<\"] NUMBER
     range-simple = NUMBER BS* <\"..\"> BS* [\"<\"] NUMBER
     range-infinite = NUMBER BS* [<\",\"> BS* NUMBER BS*] <\"...\">

     list-comprehension = list-compr-expr BS* <\"|\"> BS* list-source {BS* <','> BS* list-source}
     list-compr-expr = pipe-expr
     list-source = list-source-id BS+ <\"<-\"> BS+ list-source-value / pipe-expr
     list-source-id = ID
     list-source-value = range-def

     cons-expr = func-call-expr (BS* <'::'> BS* func-call-expr)+

     lambda-expr =  <#'\\\\'> BS* lambda-args BS+ <\"->\"> BS+ lambda-value BS* <#'\\\\'>

     lambda-args = [ID {BS+ ID}]
     <lambda-value> = pipe-expr

     <bin-expr> = comp-expr / or-expr
     or-expr = and-expr BS* \"||\" BS* bin-expr / and-expr
     and-expr = comp-expr BS* \"&&\" BS* bin-expr / comp-expr
     <comp-expr> = lt-expr / le-expr / gt-expr / ge-expr / eq-expr / ne-expr / sum-expr
     <sum-expr> = add-expr / sub-expr / cat-expr  / mult-expr
     <mult-expr> = mul-expr / div-expr / mod-expr / pow-expr / prim-expr

     add-expr = comp-expr BS* <\"+\"> BS* bin-expr
     sub-expr = comp-expr BS* <\"-\"> BS* bin-expr
     cat-expr = comp-expr BS* <\"++\"> BS* bin-expr

     mul-expr = prim-expr BS* <\"*\"> BS* mult-expr
     div-expr = prim-expr BS* <\"/\"> BS* mult-expr
     mod-expr = prim-expr BS* <\"%\"> BS* mult-expr
     pow-expr = prim-expr BS* <\"^\"> BS* mult-expr

     lt-expr = sum-expr BS* <\"<\"> BS* bin-expr
     le-expr = sum-expr BS* <\"<=\"> BS* bin-expr
     gt-expr = sum-expr BS* <\">\"> BS* bin-expr
     ge-expr = sum-expr BS* <\">=\"> BS* bin-expr
     eq-expr = sum-expr BS* <\"==\"> BS* bin-expr
     ne-expr = sum-expr BS* <\"/=\"> BS* bin-expr

     <prim-expr> = paren-expr / func-invokation / constructor / !partial-sub neg-expr / not-expr / ID / NUMBER / STRING / CHAR / range-expr / map-expr / lambda-expr


     neg-expr = \"-\"  prim-expr
     not-expr = \"not\" BS+ prim-expr

     do-expr = &<\"do\"> <\"do\"> BS* NL BS* pipe-expr BS* NL {BS* pipe-expr BS* NL} BS* <\"end\">

     func-invokation = recur / return / \"nil\"  / partial-bin / func {BS+ arg}
     func = ID / TID {\".\" TID} \".\" ID / KEYWORD


     <partial-bin> = partial-add / partial-mul / partial-sub / partial-div / partial-mod

     partial-add = <\"(+\"> {BS+ arg} BS* <\")\">
     partial-sub = <\"(-\"> {BS+ arg} BS* <\")\">
     partial-mul = <\"(*\"> {BS+ arg} BS* <\")\">
     partial-div = <\"(/\"> {BS+ arg} BS* <\")\">
     partial-mod = <\"(%\"> {BS+ arg} BS* <\")\">

     isa-type = ID BS* <\":\"> BS* TID
     type-pattern = TID BS* <\"(\"> BS* id-list BS* <\")\">

     constructor = TID BS* <\"(\"> BS* field-assign-list BS* <\")\">

     field-assign-list = field-assign {BS* <\",\"> BS* [NL BS*] field-assign}
     field-assign = ID BS+ <\"=\"> BS+ pipe-expr | pipe-expr

     map-expr = \"{\" [  map-pair {BS* <\",\"> BS* [NL] map-pair}  ] [NL BS*] \"}\"

     map-pair = BS* KEYWORD BS+ pipe-expr

     <and-op> = \"&&\"
     <or-op>  = \"||\"
     <sum-op> = \"+\" / \"++\"  / \"-\"
     <mul-op> = \"*\" / \"/\" / \"%\" / \"^\"
     <comp-op> = \"<\" / \">\" / \">=\" / \"<=\" / \"==\" / \"/=\"
     <paren-expr> = <\"(\"> BS*  pipe-expr BS* <\")\"> / tuple
     tuple = <\"(\"> BS* pipe-expr (BS* <\",\"> BS+ pipe-expr)+ <\")\">
     <bin-op> = mul-op / sum-op / comp-op

     <pipe-op> = \"|>\" / \"<|\"


     <ID-TOKEN> =  #'[\\.]?[-]?[_a-z][_0-9a-zA-Z-]*[?!]?'

     ID = !('def '|'do '|#'eager[ \r\n]'|#'else[ \r\n]'|#'end[ \r\n]'|'for '|'if '|#'in[ \r\n]'|#'lazy[ \r\n]'|#'let[ \r\n]'|#'loop[ \r\n]'|'module '|'not '|'otherwise '|'set '|#'then[ \r\n]'|'uses '|'val '|'var '|'when '|'where ') ID-TOKEN

     TID = #'[A-Z][_0-9a-zA-Z-]*'
     KEYWORD = #':[-]?[_a-z][_0-9a-zA-Z-]*[?!]?'

     <CHAR> = #\"'[^']*'\"
     STRING = #'\"[^\"]*\"'
     NUMBER = #'[0-9]+([.][0-9]+)?'

     <BS> = <#'[ \\t]'>\n

     <NL> = (COMMENT / HARD-NL)+
     <COMMENT> = <#';[^\\r\\n]*[\\n\\r]+'>
     <HARD-NL> = <#'[\\n\\r]+'>
     "))


(defn toid [id] (clojure.edn/read-string id))


(defn def-ogu-step-range
   ([a b c] (let [step (- b a)] (cons 'range [a (inc c) step])))

   ([a b c d] (let [step (- b a)] (cons 'range [a d step]))))


(defn def-ogu-simple-range
      ([a b] (cons 'range [a (inc b)]))
      ([a b c] (cons 'range [a c])))

(defn def-ogu-infinity-range
      ([start] (cons '-range-to-inf [start]))

      ([start, next] (let [step (- next start)] (cons '-range-to-inf [start step]))))

(def ast-transformations
  { :NUMBER clojure.edn/read-string
    :STRING clojure.edn/read-string
    :ID clojure.edn/read-string
    :partial-add (fn [& rest] (if (empty? rest) '+ (cons '+ rest)))
    :partial-sub (fn [& rest] (cons '- rest))
    :partial-mul (fn [& rest] (cons '* rest))
    :partial-div (fn [& rest] (cons '/ rest))
    :add-expr (fn [& rest] (cons '+ rest))
    :mul-expr (fn [& rest] (cons '* rest))
    :lt-expr (fn [& rest] (cons '< rest))
    :gt-expr (fn [& rest] (cons '> rest))
    :range-step def-ogu-step-range
    :range-simple def-ogu-simple-range
    :range-infinite def-ogu-infinity-range

    :dollar-expr (fn [a b] (cons a (list b)))
    :lambda-expr (fn [args body] (cons 'fn (cons args (list body))))
    :lambda-args vector
    :func identity
    :func-invokation (fn [& rest] (if (= 1 (count rest)) (first rest) rest))
    :val-def (fn [& rest] (cons 'def rest))
    :forward-piped-expr (fn [& rest] (cons '->> rest))
    :forward-bang-piped-expr (fn [& rest] (cons '-> rest))

    :def-args (fn [& rest] (apply vector rest))
    :definition (fn [& rest] (cons 'defn rest))

    :module-expr (fn [& rest] rest)
    :module (fn [& rest] (str (apply str (string/join \newline rest))))})

(defn transform-ast [ast]
  (insta/transform ast-transformations ast))


(def preamble "
  (require '[ogu.core :refer [println! sum union -range-to-inf]])

  ")

(defn evalue-ast [module ast]
  (let [value (transform-ast ast)]
             (load-string (str preamble value))))


(defn parse-module [options module]
  (let [arch (file module)]
    (if-not (.exists arch)
      (println "no pudo abrir archivo: " module)
      (let [text (slurp module) ast (grammar text)]
        (if (insta/failure? ast)
          (println "ERROR: " (insta/get-failure ast))
          (do (when (:print options) (println ast \newline ) (println (transform-ast ast)))
              (when (:tree  options) (insta/visualize (grammar text)))
              (when (:eval options) (evalue-ast module ast))))))))