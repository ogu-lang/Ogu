(ns ogu-lang.parser
  (:require
    [clojure.string :as string]
    [instaparse.core :as insta])
  (:use clojure.java.io)
  (:use ogu.core))

(def grammar
  (insta/parser
    "module = [module-header] {uses} {NL} {definition / type-def NL / trait-def  / module-expr}

     module-header = <'module'> BS+ module-name BS* NL

     uses = <'uses'> BS* module-name BS* {<\",\"> BS* module-name BS*} NL

     <module-name> = TID {\".\" TID} |  (ID|TID) {\".\" (ID|TID)}

     definition = [[&\"def\" <\"def\">] [\"-\"] BS+ ] ID  def-args [def-return-type] def-body [where]

     def-return-type = BS* <\"->\"> BS+ type

     type = TID | ID

     def-args = {BS+ arg} [BS* \"&\" BS+ arg]

     <arg> = &isa-type isa-type / &type-pattern type-pattern / &ID ID /  func-call-expr

     type-def = <'type'> BS+  type-constructor-def [BS* <\":\"> BS* type-constructor-def] {BS* <\":\"> BS* TID} BS*

     type-constructor-def = TID BS* <\"(\"> BS* id-list BS* <\")\">

     id-list = ID BS* {<\",\"> BS* ID}

     trait-def = <'trait'> BS+ TID BS* 'is' BS* NL trait-methods   BS* <'end'>
     trait-methods = trait-method {trait-method}
     trait-method = BS* ID {BS+ trait-method-arg} BS* NL
     trait-method-arg = TID | ID

     tuple-of-ids = <\"(\"> BS* ID {BS* <\",\"> BS+ ID} BS* <\")\">

     <module-exprs> = {module-expr}

     <module-expr> = BS* pipe-expr BS* NL


     <def-body> = body-simple / body-guard
     body-guard = NL guard+ [otherwise]
     <body-simple> = BS* <\"=\"> BS* [NL] BS* value BS* NL
     guard = BS* <\"|\"> !'otherwise' {BS+ arg} BS+ <\"=\"> BS+ value BS* NL
     otherwise = BS* <\"|\"> BS+ <\"otherwise\"> BS+ <\"=\"> BS+ value BS* NL

     where = BS+ <'where'> BS* [NL] where-equations NL

     <where-equations> = BS* where-equation {BS* <\",\"> BS* [NL BS*] where-equation}
     <where-equation> =  (equation | guard-equation)

     equation = ID [BS+ eq-args] BS+ <\"=\"> BS+ value | tuple-of-ids BS+ <\"=\"> BS+ value
     eq-args = arg {BS+ arg}

     guard-equation = ID [BS+ eq-args] BS* [NL BS*] where-body-guard
     where-body-guard = (NL where-guard)+ [NL where-otherwise]
     where-guard = BS* <\"|\"> !\"otherwise \" {BS+ arg} BS+ <\"=\"> BS+ value
     where-otherwise = BS* <\"|\"> BS+ <\"otherwise\"> BS+ <\"=\"> BS+ value


     <value> = lazy-value / eager-value
     lazy-value = &<'lazy'> <'lazy'> BS+ pipe-expr
     <eager-value> = [<'eager'> BS+] pipe-expr

     <pipe-expr> =  piped-expr / func-call-expr

     <piped-expr> = forward-piped-expr  / forward-bang-piped-expr / backward-piped-expr / backward-bang-piped-expr / dollar-expr / argless-func-call

     forward-piped-expr = func-call-expr ([NL] BS+ <\"|>\"> BS+ func-call-expr)+
     forward-bang-piped-expr = func-call-expr ([NL] BS+ <\"!>\"> BS+ func-call-expr)+
     backward-piped-expr = func-call-expr ([NL] BS+ <\"<|\"> BS+ func-call-expr)+
     backward-bang-piped-expr = func-call-expr ([NL] BS+ <\"<!\"> BS+ func-call-expr)+
     dollar-expr = func-call-expr (BS+ <\"$\"> BS+ func-call-expr)+
     argless-func-call = <\"(\"> ID  <\")\">

     <func-call-expr> = &control-expr control-expr / !control-expr lcons-expr

     recur = &'recur' <'recur'> {BS+ arg}
     return = &'return' <'return'> BS+ {arg}

     <control-expr> =  when-expr / if-expr / loop-expr  / block-expr / for-expr

     <block-expr> =  let-expr /  repeat-expr / do-expr

     <lcons-expr> =  cons-expr  /  bin-expr

     loop-expr = &'loop' <'loop'> (BS+ loop-vars-in|empty-vars-in) BS* [NL BS*] loop-body
     loop-vars-in = loop-var { BS* <\",\"> BS* [NL BS*] loop-var} BS* [NL BS*]  <'in'>
     empty-vars-in = epsilon
     <loop-body> = pipe-expr &NL
     loop-var =  ID BS+ <\"=\"> BS+ loop-var-value
     <loop-var-value> = pipe-expr

     for-expr = for-header  <'in'> for-body
     for-header = &<'for'> <'for'> BS+ ID BS+ <\"<-\"> BS+ pipe-expr BS* [NL BS*]
     for-body = BS* [NL BS*]  pipe-expr

     let-expr = &'let' <'let'> (BS+|NL BS*) let-vars  BS* [NL BS*]  let-body
     let-vars = let-vars-in
     <let-vars-in> = let-var {BS* <\",\"> BS* [NL BS*] let-var} BS* [NL BS*] <'in'>
     <let-var> = let-var-simple | let-var-tupled
     let-var-simple = ID BS+ <\"=\"> BS+ let-var-value
     let-var-tupled = let-var-tuple BS+ <\"=\"> BS+ let-var-value
     let-var-tuple =  <\"(\"> BS* (ID|let-var-tuple) {BS* <\",\"> BS* (ID|let-var-tuple)} BS* <\")\">
     <let-var-value> = pipe-expr
     <let-body> = pipe-expr &NL


     if-expr = &'if' <'if'> BS+ if-cond-expr BS* [NL BS*]  <'then'>  ([NL BS*]|BS+) then-expr [NL BS*] <'else'> ([NL BS*]|BS+) else-expr
     <then-expr> = pipe-expr BS*
     <else-expr> = pipe-expr BS*
     <if-cond-expr> = func-call-expr

     when-expr = &'when' <'when'> BS+ if-cond-expr BS* [NL BS*] <'then'> ([NL BS*]|BS+) then-expr &NL

     repeat-expr = &'repeat' <'repeat'> BS+ [repeat-var BS* {[NL] BS* <\",\"> BS* [NL BS*] repeat-var} ] &(NL|<'end'>)
     repeat-var  = [ID BS+ <\"=\"> BS+] loop-var-value

     <range-expr> = <\"[\"> BS* ( range-def | list-comprehension | simple-list | empty-range ) BS* <\"]\">
     empty-range = epsilon
     <range-def> = range-step / range-simple / range-infinite
     range-step = prim-expr BS* <\",\"> BS* prim-expr BS* <\"..\"> BS* [\"<\"] prim-expr
     range-simple = prim-expr BS* <\"..\"> BS* [\"<\"] prim-expr
     range-infinite = prim-expr BS* [<\",\"> BS* prim-expr BS*] <\"...\">

     simple-list = pipe-expr {BS* <\",\"> [BS* NL] BS+ pipe-expr}

     list-comprehension = list-compr-expr BS* <\"|\"> BS* list-source {BS* <','> BS* list-source} [list-let] [list-where]
     <list-compr-expr> = pipe-expr
     <list-source> = list-source-id BS+ <\"<-\"> BS+ list-source-value / pipe-expr
     <list-source-id> = ID / tuple-of-ids
     <list-source-value> = range-def / ID

     list-let = BS+ <\"let\"> BS+ let-var {BS* <\",\"> BS+ let-var}
     list-where = BS+ <\"where\"> BS+ if-cond-expr

     cons-expr = func-call-expr (BS* <'::'> BS* func-call-expr)+

     lambda-expr = <#'\\\\'> BS* lambda-args BS+ <\"->\"> BS+ lambda-value BS* <#'\\\\'>

     lambda-args = lambda-arg {BS+ lambda-arg}
     <lambda-arg> = ID | tupled-lambda-arg
     tupled-lambda-arg = <\"(\"> BS* lambda-arg BS* {<\",\"> BS+ lambda-arg} BS* <\")\">
     <lambda-value> = func-call-expr

     <bin-expr> =   or-expr / and-expr / comp-expr
     or-expr = bin-expr BS*  <\"||\"> BS* bin-expr
     and-expr = bin-expr BS* <\"&&\"> BS* bin-expr
     <comp-expr> = lt-expr / le-expr / gt-expr / ge-expr / eq-expr / ne-expr / sum-expr
     <sum-expr> = add-expr / addq-expr / sub-expr / subq-expr / cat-expr  / mult-expr
     <mult-expr> = mul-expr / mulq-expr / div-expr / mod-expr / pow-expr / prim-expr

     add-expr = comp-expr (BS+ <\"+\"> BS+ bin-expr)+
     addq-expr = comp-expr (BS+ <\"+'\"> BS+ bin-expr)+
     sub-expr = comp-expr BS+ <\"-\"> BS+ bin-expr
     subq-expr = comp-expr BS+ <\"-'\"> BS+ bin-expr
     cat-expr = comp-expr BS+ <\"++\"> BS+ bin-expr

     mul-expr = prim-expr (BS+ &<\"*\"> <\"*\"> BS+ mult-expr)+
     mulq-expr = prim-expr (BS+ <\"*'\"> BS+ mult-expr)+
     div-expr = prim-expr BS+ <\"/\"> BS+ mult-expr
     mod-expr = prim-expr BS+ <\"%\"> BS+ mult-expr
     pow-expr = prim-expr BS+ <\"^\"> BS+ mult-expr

     lt-expr = sum-expr BS* <\"<\"> BS* bin-expr
     le-expr = sum-expr BS* <\"<=\"> BS* bin-expr
     gt-expr = sum-expr BS* <\">\"> BS* bin-expr
     ge-expr = sum-expr BS* <\">=\"> BS* bin-expr
     eq-expr = sum-expr BS* <\"==\"> BS* bin-expr
     ne-expr = sum-expr BS* <\"/=\"> BS* bin-expr

     <prim-expr> = paren-expr / func-invokation / constructor / !partial-sub neg-expr / not-expr / ID / NUMBER / STRING / CHAR / range-expr / map-expr / lambda-expr


     neg-expr = !(NUMBER) \"-\"  prim-expr
     not-expr = \"not\" BS+ prim-expr

     do-expr = &<\"do\"> <\"do\"> BS* NL BS* pipe-expr BS* NL {BS* pipe-expr BS* NL} BS* <\"end\">

     func-invokation = recur / return / nil-value / partial-bin / func {BS+ arg}
     nil-value = <\"nil\">
     func = ID / TID  <\".\"> ID / KEYWORD


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

     map-expr = <\"{\"> [  map-pair {BS* <\",\"> BS* [NL] map-pair}  ] [NL BS*] <\"}\">

     <map-pair> = BS* KEYWORD BS+ pipe-expr

     <and-op> = \"&&\"
     <or-op>  = \"||\"
     <sum-op> = \"+\" / \"++\"  / \"-\"
     <mul-op> = \"*\" / \"/\" / \"%\" / \"^\"
     <comp-op> = \"<\" / \">\" / \">=\" / \"<=\" / \"==\" / \"/=\"
     <paren-expr> = <\"(\"> BS*  pipe-expr BS* <\")\"> / tuple
     tuple = <\"(\"> BS* pipe-expr (BS* <\",\"> BS+ pipe-expr)+ <\")\">
     <bin-op> = mul-op / sum-op / comp-op

     <pipe-op> = \"|>\" / \"<|\"


     <ID-TOKEN> =  #'[\\.]?[-]?[_a-z][_0-9a-zA-Z-]*[?!\\']*'

     ID = !('def '|'do '|#'eager[ \r\n]'|#'else[ \r\n]'|#'end[ \r\n]'|'for '|'if '|#'in[ \r\n]'|#'lazy[ \r\n]'|#'let[ \r\n]'|#'loop[ \r\n]'|'module '|'not '|'nil '|'otherwise '|'recur '|'repeat '|#'then[ \r\n]'|'uses '|'when '|'where ') ID-TOKEN

     TID = #'[A-Z][_0-9a-zA-Z-]*'
     KEYWORD = #':[-]?[_a-z][_0-9a-zA-Z-]*[?!]?'

     CHAR = #\"'[^']*'\"
     STRING = #'\"[^\"]*\"'
     NUMBER = #'[-]?[0-9]+([.][0-9]+)?([eE](-)?[0-9]+)?[NM]?'

     <BS> = <#'[ \\t]'>\n

     <NL> = (COMMENT / HARD-NL)+
     <COMMENT> = <#';[^\\r\\n]*[\\n\\r]+'>
     <HARD-NL> = <#'[\\n\\r]+'>
     "))



(defn def-ogu-step-range
   ([a b c] (let [step (cons '- [b a]) end (cons 'inc [c])] (cons 'range [a end step])))

   ([a b c d] (let [step (cons '- [b a])] (cons 'range [a d step]))))


(defn def-ogu-simple-range
      ([a b] (let [end (cons 'inc [b])] (cons 'range [a end])) )
      ([a b c] (cons 'range [a c])))

(defn def-ogu-infinity-range
      ([start] (cons '-range-to-inf [start]))

      ([start, next] (let [step (cons '- [next start])] (cons '-range-to-inf [start step]))))


(defn insert-let [eq body]
      (for [x (apply concat [eq [body]])] x))

(defn ogu-body [body eq]
      (loop [end (last eq) beg (butlast eq) result body ]
        (if (empty? end)
             result
             (recur (last beg) (butlast beg) (insert-let end result)))    )
      )

(defn ogu-priv-definition
  ([id args body]
    (if (empty? args)
      (cons 'def [id  body])
      (cons 'defn- [id args body])))

  ([id args body where]
    (let [equations (rest where) ]
         (if (empty? args)
           (cons 'def [id  (ogu-body body equations) ])
           (cons 'defn- [id args (ogu-body body equations)])))))

(defn ogu-definition
      ([id val] (cons 'def [id val]))

      ([id args body]
        (if (= "-" id)
          (ogu-definition args body)
          (if (empty? args)
            (cons 'def [id  body])
            (cons 'defn [id args body]))))

      ([id args body where]
        (if (= "-" id)
          (ogu-priv-definition args body where)
          (let [equations (rest where) ]
               (if (empty? args)
                 (cons 'def [id  (ogu-body body equations) ])
                 (cons 'defn [id args (ogu-body body equations) ]))))
        )

      ([id args ret body where]
        (if (= "-" id)
          (ogu-priv-definition args ret body where)
          (ogu-definition id args body where)))

      ([min id args ret body where]
          (ogu-priv-definition id args body where)))


(defn ogu-guards [& guards] (cons 'cond (apply concat guards)))

(defn ogu-guard [a b] [a b])

(defn ogu-id
      ([id] id)
      ([tid id] (clojure.edn/read-string (str tid \/ id))))

(defn to-char [n]
      (let [l (count n) s (subs n 1 (dec l))]
           (clojure.edn/read-string (str \\ s))))

(defn ogu-repeat-var
      ([v] v)
      ([nn v] v))

(defn ogu-equation
      ([idf val] (cons 'let [(vec [idf val])]))
      ([idf args val]
        (if (empty? args)
          (cons 'let [(vec [idf val])] )
          (list 'letfn [(list idf args val)])  )))

(defn ogu-flatten-last-while [v]
      (let [end (last v) rest (butlast v) while (:when end)]
           (if (empty? while)
             v
             (conj (conj (vec rest) :when)  while))))


(defn ogu-uses
      ([ns & rest]
        (if (empty? rest)
          (cons ':require '[ogu.core :refer :all]))
          rest))

(def ast-transformations
  {:NUMBER                   clojure.edn/read-string
   :STRING                   clojure.edn/read-string
   :KEYWORD                  clojure.edn/read-string
   :CHAR                     to-char
   :ID                       clojure.edn/read-string
   :TID                      clojure.edn/read-string
   :partial-add              (fn [& rest] (if (empty? rest) '+ (cons '+ rest)))
   :partial-sub              (fn [& rest] (if (empty? rest) '- (cons '- rest)))
   :partial-mul              (fn [& rest] (if (empty? rest) '* (cons '* rest)))
   :partial-div              (fn [& rest] (if (empty? rest) '/ (cons '/ rest)))
   :add-expr                 (fn [& rest] (cons '+ rest))
   :addq-expr                (fn [& rest] (cons '+' rest))
   :sub-expr                 (fn [& rest] (cons '- rest))
   :subq-expr                (fn [& rest] (cons '-' rest))
   :mul-expr                 (fn [& rest] (cons '* rest))
   :mulq-expr                (fn [& rest] (cons '*' rest))
   :div-expr                 (fn [& rest] (cons '/ rest))
   :mod-expr                 (fn [& rest] (cons 'mod rest))
   :neg-expr                 (fn [& rest] (cons '- rest))


   :pow-expr                 (fn [& rest] (cons 'pow rest))

   :cat-expr                 (fn [& rest] (cons 'concat rest))

   :lt-expr                  (fn [& rest] (cons '< rest))
   :le-expr                  (fn [& rest] (cons '<= rest))
   :gt-expr                  (fn [& rest] (cons '> rest))
   :ge-expr                  (fn [& rest] (cons '>= rest))
   :eq-expr                  (fn [& rest] (cons '= rest))
   :ne-expr                  (fn [& rest] (cons 'not= rest))
   :and-expr                 (fn [& rest] (cons 'and rest))
   :or-expr                  (fn [& rest] (cons 'or rest))
   :range-step               def-ogu-step-range
   :range-simple             def-ogu-simple-range
   :range-infinite           def-ogu-infinity-range
   :cons-expr                (fn [& rest] (cons 'cons rest))
   :lazy-value               (fn [& rest] (cons 'lazy-seq rest))

   :let-var-tuple            (fn [& rest] (vec rest))
   :let-var-tupled           (fn [ids val] [ids val])
   :let-vars                 (fn [& rest] (vec (apply concat rest)))
   :let-var-simple           (fn [id val] [id val])
   :let-expr                 (fn [& rest] (cons 'let rest))

   :otherwise                (fn [& rest] [:else (first rest)])
   :where-otherwise          (fn [& rest] [:else (first rest)])
   :guard                    ogu-guard
   :where-guard              ogu-guard
   :body-guard               ogu-guards
   :where-body-guard         ogu-guards

   :equation                 ogu-equation
   :guard-equation           ogu-equation

   :if-expr                  (fn [& rest] (cons 'if rest))

   :loop-vars-in             (fn [& args] (vec (apply concat args)))
   :loop-var                 (fn [var val] [var val])
   :loop-expr                (fn [& rest] (cons 'loop rest))

   :repeat-expr              (fn [& rest] (cons 'recur rest))
   :repeat-var               ogu-repeat-var

   :list-where               (fn [& rest] {:when (first rest)})

   :list-comprehension       (fn [expr & rest] (cons 'for [(ogu-flatten-last-while (vec rest)) expr]))

   :empty-range              vector

   :do-expr                  (fn [& rest] (cons 'do rest))

   :when-expr                (fn [& rest] (cons 'when rest))

   :map-expr                 (fn [& rest] (apply hash-map rest))

   :nil-value                (fn [] nil)

   :tuple                    (fn [& rest] (vec rest))
   :tupled-lambda-arg        (fn [& rest] (vec rest))

   :module-header            (fn [ns] (cons 'ns [ns '(:require [ogu.core :refer :all])]))

   :simple-list              (fn [& rest] (vec rest))
   :tuple-of-ids             (fn [& rest] (vec rest))

   :dollar-expr              (fn [a b] (cons a (list b)))
   :lambda-expr              (fn [args body] (cons 'fn (cons args (list body))))
   :lambda-args              vector
   :func                     ogu-id
   :func-invokation          (fn [& rest] (if (= 1 (count rest)) (first rest) rest))
   :backward-piped-expr      (fn [& rest] (cons '->> (reverse rest)))
   :backward-bang-piped-expr (fn [& rest] (cons '-> (reverse rest)))
   :forward-piped-expr       (fn [& rest] (cons '->> rest))
   :forward-bang-piped-expr  (fn [& rest] (cons '-> rest))

   :recur                    (fn [& rest] (cons 'recur rest))


   :isa-type                 (fn [var type] [(symbol (str \^ type)) var])

   :eq-args                  (fn [& rest] (vec  (flatten rest) ))
   :def-args                 (fn [& rest] (vec  (flatten rest) ))
   :definition               ogu-definition

   :argless-func-call        (fn [& rest] rest)


   :module-expr              (fn [& rest] rest)
   :module                   (fn [& rest] (str (apply str (string/join \newline rest))))})


(defn classify-form [[p form]]
      (cond
        (symbol? form) { (str "expr-" (md5 (str (uuid)))) {:form form :pos p}}
        :else (if (or (= (first form) 'def) (= (first form) 'defn))
                { (str "defv-" (second form)) {:form form :pos p}   }
                { (str "expr-" (md5 (str (uuid)))) {:form form :pos p}})))

(defn merge-variadic-function [fun]
      (println (count fun))
      (println fun)
      fun)

(defn merge-functions [[k v]]
      (cond
        (string/starts-with? k "defv-") (merge-variadic-function v)
        :else v))

(defn merge-variadic [source]
      (let [tree (clojure.edn/read-string (str "[ " source "]"))
            forms (apply merge-with concat (mapv classify-form (ogu.core/zip (ogu.core/-range-to-inf 1) tree)))  ]
           (apply str (doall (map #(str (:form %) \newline) (sort-by :pos (map merge-functions forms)))) ) )  )

(defn transform-ast [ast]
   (merge-variadic (insta/transform ast-transformations ast)))

(def preamble "
  (require '[ogu.core :refer :all])

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