(ns ogu-lang.parser
    (:require
      [clojure.string :as string]
      [instaparse.core :as insta]
      [ogu.core :as ogu])
    (:use clojure.java.io))

(def grammar
  (insta/parser
    "module = [module-header] {import-static} {NL} {definition / dispatch / method-definition / val-def NL / type-def  / trait-def / extension / module-expr}

     module-header = [NL] <'module'> BS+ module-name (NL+ {require|import}| BS*  NL)

     require = BS* <'require'> BS* module-use {BS* <\",\"> (NL BS*|BS+) module-use } NL

     module-use = module-name [BS+ 'as' BS+ (ID|TID) | BS+ 'refer' ((BS+ 'all') | (BS+ <'['> id-list <']'>))]

     import = !import-static BS* <'import'> BS+ import-list {BS* <\",\"> (NL BS*|BS+) import-list } NL

     import-static =  BS* <'import'> BS+ <'static'> BS+ module-name BS* <\"(\"> BS* (ID|TID) {BS* <\",\"> (NL BS*|BS+) (ID|TID) } BS* <\")\"> NL

     module-name = TID {\".\" TID} |  (ID|TID) {\".\" (ID|TID)}

     import-list = module-name {BS+ TID}

     definition = func-definition

     <func-definition> =  (<\"def\"> [\"-\"] BS+ ID | [\"-\" BS+] ID)  def-args  def-body [where]

     method-definition =  <\"def\"> [\"-\"] BS+ ID BS+ (lcons-expr|method-otherwise) BS* <\"?\"> def-args def-body [where]

     method-otherwise = <\"otherwise\">

     dispatch = &\"dispatch\" <\"dispatch\"> BS+ ID BS+ <\"on\"> BS+ func-call-expr NL

     val-def = &(\"val\"|\"let\") <\"val\"|\"let\"> BS+ ID BS+ <\"=\"> BS+ pipe-expr

     type = TID | ID

     def-args = {BS+ arg} [BS* rest-args]

     <rest-args> = and-token BS+ arg

     <arg> = &isa-type isa-type / &type-pattern type-pattern / &ID ID / as-arg / func-call-expr

     as-arg = func-call-expr BS+ <'as'> BS+ ID

     type-def = <'type'> BS+ type-constructor-def (traits-for-type|BS* NL)

     <traits-for-type> = trait-for-type-1 {trait-for-type-n}

     <trait-for-type-1> = [NL] BS+ <\"as\"> BS+ TID BS* NL (trait-method-impl)+

     <trait-for-type-n> = BS+ <\"as\"> BS+ TID BS* NL (trait-method-impl)+

     trait-method-impl = BS+ [<\"def\"> BS+] ID trait-method-impl-args def-body [where]

     trait-method-impl-args = {BS+ trait-method-impl-arg}

     <trait-method-impl-arg> = ID

     <type-constructor-def> = class-constructor-def | record-constructor-def

     class-constructor-def = TID BS* <\"(\"> BS* [class-id-list BS*] <\")\">

     record-constructor-def = TID BS* <\"{\"> BS* [record-id-list BS*] <\"}\">\n

     class-id-list = class-member-id-def BS* {<\",\"> BS+ class-member-id-def}

     class-member-id-def = ID | \"var\" BS+ ID | \"val\" BS+ ID

     <record-id-list> = id-list

     id-list = ID BS* {<\",\"> BS* ID}

     trait-def = <'trait'> BS+ TID BS* <'is'> BS* NL trait-methods

     <trait-methods> = trait-method {trait-method}

     trait-method = BS+ [<\"def\"> BS+] ID trait-method-args BS* NL

     trait-method-args = {BS+ trait-method-arg}

     <trait-method-arg> = ID

     extension = <\"extend\"> BS+ TID traits-for-type

     tuple-of-ids = <\"(\"> BS* ID {BS* <\",\"> BS+ ID} BS* <\")\">

     <module-exprs> = {module-expr}

     <module-expr> = BS* pipe-expr BS* NL

     <def-body> = body-simple  / body-guard

     body-guard = BS* ([NL] guard) {guard} [otherwise]

     <body-simple> = BS* <\"=\"> BS* [NL] BS* value BS* NL

     guard = BS* <\"|\"> !'otherwise' (BS+ arg)+ BS+ <\"=\"> BS+ value BS* NL

     otherwise = BS* <\"|\"> BS+ <\"otherwise\"> BS+ <\"=\"> BS+ value BS* NL

     where = BS+ <'where'> BS* [NL] where-equations NL

     <where-equations> = BS* where-equation {(BS* <\",\"> BS* [NL BS*] | BS* NL BS+) where-equation}

     <where-equation> =  (equation | guard-equation)

     equation = ID [BS+ eq-args] BS+ <\"=\"> BS+ value | tuple-of-ids BS+ <\"=\"> BS+ value

     eq-args = arg {BS+ arg}

     guard-equation = ID [BS+ eq-args] BS* [NL BS*] where-body-guard

     where-body-guard = [NL] where-guard {NL where-guard} [NL where-otherwise]

     where-guard = BS* <\"|\"> !\"otherwise \" (BS+ arg)+ BS+ <\"=\"> BS+ value

     where-otherwise = BS* <\"|\"> BS+ <\"otherwise\"> BS+ <\"=\"> BS+ value

     <value> = lazy-value / eager-value

     lazy-value = &<'lazy'> <'lazy'> BS+ pipe-expr

     <eager-value> = [<'eager'> BS+] pipe-expr

     proxy-def = <'proxy'> BS* proxy-extend-list (NL BS*| BS+) <'is'> NL proxy-method-impl+ BS* <\"end\">

     proxy-extend-list = proxy-type [proxy-super-args] {BS* <\"&\"> BS+ proxy-type}

     <proxy-type> = ID | TID

     proxy-super-args = BS* <\"(\"> [BS* lcons-expr {BS* <\",\"> BS+ lcons-expr} BS*] <\")\">

     proxy-method-impl = BS+ [<\"def\"> BS+] ID proxy-method-args def-body [where]

     proxy-method-args = {BS+ proxy-method-arg}

     <proxy-method-arg> = ID

     <pipe-expr> =  piped-expr / func-call-expr

     <piped-expr> = forward-piped-expr  / forward-first-piped-expr / backward-piped-expr / backward-bang-piped-expr / doto-expr / dollar-expr / expr-seq

     forward-piped-expr = func-call-expr ([NL] BS+ <\"|>\"> BS+ func-call-expr)+

     forward-first-piped-expr = func-call-expr ([NL] BS+ <\">|\"> BS+ func-call-expr)+

     backward-piped-expr = func-call-expr ([NL] BS+ <\"<|\"> BS+ func-call-expr)+

     backward-bang-piped-expr = func-call-expr ([NL] BS+ <\"<!\"> BS+ func-call-expr)+

     doto-expr = func-call-expr ([NL] BS+ <\"!>\"> BS+ func-call-expr)+

     dollar-expr = func-call-expr (BS+ <\"$\"> BS+ func-call-expr)+

     argless-func-call = <\"(\"> ID  <\")\">

     <func-call-expr> = &control-expr control-expr / !control-expr lcons-expr

     recur = &'recur' <'recur'> {BS+ arg}

     <control-expr> =  when-expr / if-expr / cond-expr / loop-expr  / block-expr / for-expr / while-expr / sync-expr / lambda-expr / using-expr / do-expr


     cond-expr = <\"cond\"> (cond-pair)+[cond-otherwise]

     cond-pair = (BS+|NL BS*) lcons-expr BS+ <\"->\"> BS+ pipe-expr BS* &NL

     cond-otherwise = (BS+|NL BS*) <\"otherwise\"> BS+ <\"->\"> BS+ pipe-expr BS* &NL

     sync-expr = <\"sync\"> (BS+ pipe-expr | BS* NL BS+ pipe-expr) &NL

     <block-expr> =  let-expr /  repeat-expr / begin-end-expr / var-expr / proxy-def

     <lcons-expr> =  cons-expr  /  bin-expr

     loop-expr = &'loop' <'loop'> (BS+ loop-vars-in|empty-vars-in) BS* [NL BS*] loop-body

     loop-vars-in = loop-var {(BS* <\",\"> BS* [NL BS*] | BS* NL BS+) loop-var} BS* [NL BS*]  <'in'>

     empty-vars-in = epsilon

     <loop-body> = pipe-expr &NL

     loop-var =  ID BS+ <\"=\"> BS+ loop-var-value

     <loop-var-value> = pipe-expr

     while-expr = &'while' <'while'> BS+ func-call-expr BS+ <'do'> BS* [NL BS*] pipe-expr &NL

     for-expr =  &<'for'> <'for'> (BS+|NL BS*)  for-vars  BS* [NL BS*] for-body

     for-vars = for-vars-in

     <for-vars-in> = for-var {(BS* NL BS*| BS* <\",\"> BS [NL BS*]) for-var} BS* [NL BS*] <'do'>

     <for-var> = for-var-simple | for-var-tupled

     for-var-simple = ID BS+ <\"<-\"> BS+ for-var-value

     for-var-tupled = for-var-tuple BS+ <\"<-\"> BS+ for-var-value

     for-var-tuple = <\"(\"> BS* (ID|for-var-tuple) {BS* <\",\"> BS* (ID|for-var-tuple)} BS* <\")\">

     <for-var-value> = pipe-expr

     <for-body> = pipe-expr &NL

     using-expr = <'using'> (BS+|NL BS*) using-vars-in BS* [NL BS*] using-body


     <using-vars-in> = using-var {(BS* NL BS*| BS* <\",\"> BS* [NL BS*]) using-var} BS* [NL BS*] <'in'>

     <using-var> = let-var-simple

     <using-body> = pipe-expr &NL

     let-expr = &'let' <'let'> (BS+|NL BS*) let-vars  BS* [NL BS*]  let-body

     let-vars = let-vars-in

     <let-vars-in> = let-var {(BS* NL BS*| BS* <\",\"> BS* [NL BS*]) let-var} BS* [NL BS*] <'in'>

     <let-var> = let-var-simple | let-var-tupled

     let-var-simple = ID BS+ <\"=\"> BS+ let-var-value

     let-var-tupled = let-var-tuple BS+ <\"=\"> BS+ let-var-value

     let-var-tuple =  <\"(\"> BS* (ID|let-var-tuple) {BS* <\",\"> BS* (ID|let-var-tuple)} BS* <\")\">

     <let-var-value> = pipe-expr

     <let-body> = pipe-expr &NL

     var-expr = &'var' <'var'> (BS+|NL BS*) var-vars BS* [NL BS*] var-body

     var-vars = var-vars-in

     <var-vars-in> = var-var {(BS* NL BS*| BS* <\",\"> BS* [NL BS*]) var-var} BS* [NL BS*] <'in'>

     <var-var> = var-var-simple | var-var-tupled

     var-var-simple = ID BS+ <\"=\"> BS+ var-var-value

     var-var-tupled = var-var-tuple BS+ <\"=\"> BS+ var-var-value

     var-var-tuple = <\"(\"> BS* (ID|var-var-tuple) {BS* <\",\"> BS* (ID|var-var-tuple)} BS* <\")\">

     <var-var-value> = pipe-expr

     <var-body> = pipe-expr &NL

     at-expr = &'@' <'@'> ID [BS+ <\"=\"> BS+ pipe-expr &NL]

     bang-expr = &'!' <'!'> ID [<\".\"> ID] (BS+ \"=\" BS+ pipe-expr &NL | <\".\">  ID)

     if-expr = &'if' <'if'> BS+ if-cond-expr BS* [NL BS*]  <'then'>  ([NL BS*]|BS+) then-expr [NL BS*] <'else'> ([NL BS*]|BS+) else-expr

     <then-expr> = pipe-expr BS*

     <else-expr> = pipe-expr BS*

     <if-cond-expr> = func-call-expr

     when-expr = &'when' <'when'> BS+ if-cond-expr BS* [NL BS*] <'do'> ([NL BS*]|BS+) then-expr &NL

     repeat-expr = &'repeat' <'repeat'> (BS* NL|BS+) [repeat-var  {(BS* <\",\"> BS* [NL BS*] | BS* NL BS+) repeat-var} ] &(NL|<'end'>)

     repeat-var  = [ID BS+ <\"=\"> BS+] loop-var-value

     <range-expr> = <\"[\"> BS* ( range-def | list-comprehension | simple-list | empty-range ) BS* <\"]\">

     empty-range = epsilon

     <range-def> = range-step / range-simple / range-infinite

     range-step = prim-expr BS* <\",\"> BS* prim-expr BS* <\"..\"> BS* [\"<\"] prim-expr

     range-simple = prim-expr BS* <\"..\"> BS* [\"<\"] prim-expr

     range-infinite = prim-expr BS* [<\",\"> BS* prim-expr BS*] <\"...\">

     simple-list = pipe-expr {BS* <\",\"> [BS* NL] BS+ pipe-expr} [BS+ and-token BS+ pipe-expr]

     and-token = <\"&\">

     list-comprehension = list-compr-expr BS* <\"|\"> BS* list-source {BS* <','> BS* list-source} [list-let] [list-where]

     <list-compr-expr> = pipe-expr

     <list-source> = list-source-id BS+ <\"<-\"> BS+ list-source-value / pipe-expr

     <list-source-id> = ID / tuple-of-ids

     <list-source-value> = range-expr / ID / simple-list

     list-let = BS+ <\"let\"> BS+ let-var {BS* <\",\"> BS+ let-var}

     list-where = BS+ <\"where\"> BS+ if-cond-expr

     cons-expr = func-call-expr (BS* <'::'> BS* func-call-expr)+

     lambda-expr = <#'\\\\'> BS* lambda-args BS+ <\"->\"> BS+ lambda-value BS* <#'\\\\'>

     lambda-args = lambda-arg {BS+ lambda-arg} [BS+ and-token BS+ lambda-arg]

     <lambda-arg> = ID | tupled-lambda-arg

     tupled-lambda-arg = <\"(\"> BS* lambda-arg BS* {<\",\"> BS+ lambda-arg} BS* <\")\">

     <lambda-value> = &func-call-expr func-call-expr

     <bin-expr> =  logical-expr

     <logical-expr> = or-expr / and-expr / comp-expr

     or-expr = logical-expr BS*  (<\"||\"> BS* logical-expr)+

     and-expr = comp-expr BS* (<\"&&\"> BS* comp-expr)

     <comp-expr> = lt-expr / le-expr / gt-expr / ge-expr / eq-expr / ne-expr / sum-expr

     lt-expr = sum-expr (BS+ <\"<\">  BS+ sum-expr)+

     le-expr = sum-expr (BS+ <\"<=\"> BS+ sum-expr)+

     gt-expr = sum-expr (BS+ <\">\">  BS+ sum-expr)+

     ge-expr = sum-expr (BS+ <\">=\"> BS+ sum-expr)+

     eq-expr = sum-expr (BS+ <\"==\"> BS+ sum-expr)+

     ne-expr = sum-expr (BS+ <\"/=\"> BS+ sum-expr)+

     <sum-expr> = add-expr / addq-expr / sub-expr / subq-expr / cat-expr / mult-expr

     add-expr  = mult-expr  (BS+ <\"+\">  BS+ mult-expr)+

     addq-expr = mult-expr (BS+ <\"+'\"> BS+ mult-expr)+

     sub-expr  = mult-expr  (BS+ <\"-\">  BS+ mult-expr)+

     subq-expr = mult-expr (BS+ <\"-'\"> BS+ mult-expr)+

     cat-expr  = mult-expr  (BS+ <\"++\"> BS+ mult-expr)+

     <mult-expr> =  mul-expr / mulq-expr / div-expr / mod-expr / factor-expr

     mul-expr  = mult-expr (BS+ &<\"*\"> <\"*\">  BS+ mult-expr)+

     mulq-expr = mult-expr (BS+ <\"*'\">          BS+ mult-expr)+

     div-expr  = mult-expr (BS+ <\"/\">           BS+ mult-expr)+

     mod-expr  = mult-expr (BS+ <\"%\">           BS+ mult-expr)+

     <factor-expr> = pow-expr / prim-expr

     pow-expr = prim-expr BS+ <\"^\"> BS+ factor-expr

     <prim-expr> = &partial-bin partial-bin / argless-func-call / paren-expr / func-invokation / constructor-call / record-constructor-call / !partial-sub neg-expr / not-expr / ID / KEYWORD / NUMBER / FSTRING / STRING / CHAR / range-expr / map-expr / set-expr / at-expr / bang-expr

     neg-expr = !(NUMBER) \"-\"  prim-expr

     not-expr = \"not\" BS+ prim-expr

     begin-end-expr = &<\"begin\"> <\"begin\"> BS* NL BS* pipe-expr BS* NL {BS* pipe-expr BS* NL} BS* <\"end\">

     do-expr = &<\"do\"> <\"do\"> BS* NL BS* pipe-expr  BS* NL {BS* pipe-expr BS* NL} BS* <\"end\">

     expr-seq = (BS* pipe-expr BS* <\";\">[BS* NL] )+ BS* pipe-expr

     func-invokation = recur  / nil-value  / func ({BS+ arg} / <'('> func {BS+ arg} &')' <')'> / [BS arg {BS* <\"~\"> BS+ arg}])

     nil-value = <\"nil\">

     func = ID /  KEYWORD / !constructor (TID|ID) {<\".\"> (TID|ID)} [<\"/\"> ID]

     <partial-bin> = partial-add / partial-mul / partial-sub / partial-div / partial-mod / partial-eq

     partial-add = <\"(+\"> {BS+ arg} BS* <\")\">

     partial-sub = <\"(-\"> {BS+ arg} BS* <\")\">

     partial-mul = <\"(*\"> {BS+ arg} BS* <\")\">

     partial-div = <\"(/\"> {BS+ arg} BS* <\")\">

     partial-mod = <\"(%\"> {BS+ arg} BS* <\")\">

     partial-eq = <\"(==\"> {BS+ arg} BS* <\")\">\n

     isa-type = ID BS* <\":\"> BS* TID

     type-pattern = TID BS* <\"(\"> BS* id-list BS* <\")\">

     <constructor-call> = constructor | jvm-constructor

     constructor = TID BS* <\"(\"> BS* [field-assign-list BS*] <\")\">

     record-constructor-call = TID BS* <\"{\"> BS* [field-assign-list BS*] <\"}\">

     jvm-constructor = <\"new\"> BS+ TID BS* <\"(\"> BS* [expr-list BS*] <\")\">

     <field-assign-list> = field-assign {BS* <\",\"> BS* [NL BS*] field-assign}

     field-assign = ID BS+ <\"=\"> BS+ pipe-expr | pipe-expr

     <expr-list> = pipe-expr {BS* <\",\"> BS+ pipe-expr}

     map-expr = <\"{\"> [  map-pair {BS* <\",\"> BS* [NL] map-pair} [BS+ as-token BS+ ID] ] [NL BS*] <\"}\">

     as-token = ':as'

     <map-pair> = BS* (KEYWORD BS+ pipe-expr | TID BS+ pipe-expr | ID BS+ pipe-expr | pipe-expr BS+ pipe-expr)

     set-expr = <\"#{\"> [  set-element {BS* <\",\"> BS* [NL] set-element} ] [NL BS*] <\"}\">

     <set-element> = BS* (KEYWORD | ID | pipe-expr)

     <and-op> = \"&&\"

     <or-op>  = \"||\"

     <sum-op> = \"+\" / \"++\"  / \"-\"

     <mul-op> = \"*\" / \"/\" / \"%\" / \"^\"

     <comp-op> = \"<\" / \">\" / \">=\" / \"<=\" / \"==\" / \"/=\"

     <paren-expr> = <\"(\"> BS*  pipe-expr BS* <\")\"> / tuple

     tuple = <\"(\"> pipe-expr {BS* <\",\"> [BS* NL] BS+ pipe-expr} [BS+ and-token BS+ pipe-expr] <\")\">

     <bin-op> = mul-op / sum-op / comp-op

     <pipe-op> = \"|>\" / \"<|\"

     <ID-TOKEN> =  #'[\\.]?[_a-z-*][_0-9a-zA-Z-*]*[?!\\']*'

     ID = !(COMMENT|'++ '|'+ '|\"+' \"|'* '|\"*' \"|'- '|\"-' \"|'as '|#'begin[ \r\n]'|#'cond[ \r\n]'|'def '|'dispatch '|#'do[ \r\n]'|#'eager[ \r\n]'|#'else[ \r\n]'|#'end[ \r\n]'|'extend '|'for '|'if '|#'in[ \r\n]'|'imports '|#'lazy[ \r\n]'|#'let[ \r\n]'|#'loop[ \r\n]'|'module '|'new '|'not '|'nil '|'otherwise '|'proxy '|'recur '|'refer '|'repeat '|'require '|'static '|#'then[ \r\n]'|'trait '|'using '|'val '|'when '|'where '|'while ') ID-TOKEN

     TID = #'[A-Z][_0-9a-zA-Z-]*'

     KEYWORD = #':[-]?[_a-z][_0-9a-zA-Z-]*[?!]?'

     CHAR = #\"'[^']*'\"

     STRING = #'\"[^\"]*\"'

     FSTRING = #'#\"[^\"]*\"'

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
      ([a b] (let [end (cons 'inc [b])] (cons 'range [a end])))
      ([a b c] (cons 'range [a c])))

(defn def-ogu-infinity-range
      ([start] (cons '-range-to-inf [start]))

      ([start, next] (let [step (cons '- [next start])] (cons '-range-to-inf [start step]))))


(defn insert-let [eq body]
      (for [x (apply concat [eq [body]])] x))

(defn ogu-body [body eq]
      (loop [end (last eq) beg (butlast eq) result body]
            (if (empty? end)
              result
              (recur (last beg) (butlast beg) (insert-let end result))))
      )

(defn ogu-priv-definition
      ([id args body]
        (if (empty? args)
          (cons 'defn- [id [] body])
          (cons 'defn- [id args body])))

      ([id args body where]
        (let [equations (rest where)]
             (if (empty? args)
               (cons 'defn- [id [] (ogu-body body equations)])
               (cons 'defn- [id args (ogu-body body equations)])))))

(defn ogu-definition
      ([id val] (cons 'defn [id [] val]))

      ([id args body]
        (if (= "-" id)
          (ogu-definition args body)
          (if (empty? args)
            (cons 'defn [id [] body])
            (cons 'defn [id args body]))))

      ([id args body where]
        (if (= "-" id)
          (ogu-priv-definition args body where)
          (if (and (vector? where) (= :where (first where)))
            (let [equations (rest where)]
                 (if (empty? args)
                   (cons 'defn [id [] (ogu-body body equations)])
                   (cons 'defn [id args (ogu-body body equations)])))
            (ogu-definition id args where))))

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
      ([tid id] (clojure.edn/read-string (str tid \/ id)))
      ([tid id & rest] nil))

(defn ogu-to-char [n]
      (let [l (count n) s (subs n 1 (dec l))]
           (cond
             (= s "\t") \tab
             (= s "\n") \newline
             (.startsWith s "\\" ) (clojure.edn/read-string s)
             :else (clojure.edn/read-string (str \\ s)))))


(defn ogu-field-assign
      ([v] v)
      ([nn v] v))

(defn ogu-repeat-expr [& rest]
      (let [has-assign (some #(= 3 (count %)) rest)]
           (if-not has-assign
                   (cons 'recur (for [[_ v] rest] v))
                   (list 'let
                         (vec (apply concat (for [[_ n v] (filter #(= 3 (count %)) rest)] [n v])))
                         (cons 'recur (for [[_ v] rest] v))))))

(defn ogu-equation
      ([idf val] (cons 'let [(vec [idf val])]))
      ([idf args val]
        (if (empty? args)
          (cons 'let [(vec [idf val])])
          (list 'letfn [(list idf args val)]))))

(defn ogu-flatten-last-while [v]
      (let [end (last v) rest (butlast v) while (:when end)]
           (if (empty? while)
             v
             (conj (conj (vec rest) :when) while))))


(defn ogu-at-expr
      ([v] (cons 'deref [v]))
      ([v s] (cons 'var-set [v s])))


(defn ogu-bang-expr
      ([o f] (cons (symbol (str \. f)) [o]))
      ([f _ v] (cons 'set! [f v])))

(defn ogu-module-use
      ([m] m)
      ([m op rest]
        (cond
          (= op "as") [m :as rest]
          (and (= op "refer") (= rest "all")) [m :refer :all]
          (= op "refer") [m :refer rest])))

(defn ogu-constructor
      ([tid] (list (clojure.edn/read-string (str tid \.))))
      ([tid & args] (cons (clojure.edn/read-string (str "->" tid)) args)))

(defn ogu-def-args [& rest]
      (vec (for [x rest] x)))


(defn ogu-class-member-id-def
      ([x] x)
      ([v x]
        (cond
          (= v "val") x
          (= v "var") [:mutable x]
          :else x)))

(defn ogu-type-def [& def-type]
   (let [header (first def-type) body (rest def-type) tag (first header) name (second header) fields (drop 2 header)]
     (cond
       (= tag :class-constructor-def) (cons '-def-ogu-type- (concat [name] fields body))
       :else (cons 'defrecord (concat [name] fields body)))))


(defn ogu-fstring [s]
      (let [fs (str "(ogu.core/fmt " (subs s 1) ")")]
           (clojure.edn/read-string fs)))

(def ast-transformations
  {:NUMBER                   clojure.edn/read-string
   :STRING                   clojure.edn/read-string
   :FSTRING                  ogu-fstring
   :KEYWORD                  clojure.edn/read-string
   :CHAR                     ogu-to-char
   :ID                       clojure.edn/read-string
   :TID                      clojure.edn/read-string
   :partial-add              (fn [& rest] (if (empty? rest) '+ (cons '+ rest)))
   :partial-sub              (fn [& rest] (if (empty? rest) '- (cons '- rest)))
   :partial-mul              (fn [& rest] (if (empty? rest) '* (cons '* rest)))
   :partial-div              (fn [& rest] (if (empty? rest) '/ (cons '/ rest)))
   :partial-mod              (fn [& rest] (if (empty? rest) 'mod (cons 'mod rest)))
   :partial-eq               (fn [& rest] (if (empty? rest) '= (cons '= rest)))

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

   :using-expr               (fn [& rest] (cons 'with-open rest))

   :var-var-tuple            (fn [& rest] (vec rest))
   :var-var-tupled           (fn [ids val] [ids val])
   :var-vars                 (fn [& rest] (vec (apply concat rest)))
   :var-var-simple           (fn [id val] [id val])
   :var-expr                 (fn [& rest] (cons 'with-local-vars rest))


   :for-var-tuple            (fn [& rest] (vec rest))
   :for-var-tupled           (fn [ids val] [ids val])
   :for-vars                 (fn [& rest] (vec (apply concat rest)))
   :for-var-simple           (fn [id val] [id val])
   :for-expr                 (fn [& rest] (cons 'doseq rest))

   :while-expr               (fn [& rest] (cons 'while rest))

   :cond-expr                (fn [& rest] (cons 'cond (apply concat rest)))
   :cond-pair                 (fn [& rest] rest)

   :field-assign             ogu-field-assign

   :dispatch                 (fn [& rest] (cons 'defmulti rest))
   :method-definition        (fn [& rest] (cons 'defmethod rest))

   :otherwise                (fn [& rest] [:else (first rest)])
   :where-otherwise          (fn [& rest] [:else (first rest)])
   :cond-otherwise           (fn [& rest] [:else (first rest)])
   :method-otherwise         (fn [& rest] ':default)

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

   :repeat-expr              ogu-repeat-expr

   :and-token                (fn [& rest] '&)
   :as-token                 (fn [& rest] ':as)


   :list-where               (fn [& rest] {:when (first rest)})

   :list-comprehension       (fn [expr & rest] (cons 'for [(vec (ogu-flatten-last-while rest)) expr]))

   :empty-range              vector

   :begin-end-expr           (fn [& rest] (cons 'do rest))
   :do-expr                  (fn [& rest] (cons 'do rest))

   :when-expr                (fn [& rest] (cons 'when rest))

   :map-expr                 (fn [& rest] (into {} (map vec (partition 2 rest))))

   :set-expr                 (fn [& rest] (into #{} rest))

   :nil-value                (fn [] nil)


   :tuple                    (fn [& rest] (vec rest))
   :tupled-lambda-arg        (fn [& rest] (vec rest))

   :id-list                  (fn [& rest] (vec rest))
   :import-list              (fn [& rest] rest)
   :require                  (fn [& rest] (cons ':require rest))
   :import                   (fn [& rest] (cons ':import rest))
   :import-static            (fn [& rest] (cons 'import-static rest))
   :module-use               ogu-module-use
   :module-name              (fn [& rest] (clojure.edn/read-string (apply str rest)))
   :module-header            (fn [ns & rest] (cons 'ns (cons ns (cons '(:require [ogu.core :refer :all]) rest))))
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
   :forward-first-piped-expr (fn [& rest] (cons '-> rest))
   :doto-expr                (fn [& rest] (cons 'doto rest))


   :isa-type                 (fn [obj t]  (list obj ':guard (list 'partial 'isa-type? t)  :as obj))

   :jvm-constructor          (fn [& rest] (cons 'new rest))
   :constructor              ogu-constructor
   :record-constructor-call  ogu-constructor
   :at-expr                  ogu-at-expr
   :bang-expr                ogu-bang-expr
   :sync-expr                (fn [& rest] (cons 'dosync rest))

   :proxy-def                (fn [& rest] (cons 'proxy (apply concat rest)))
   :proxy-method-impl        (fn [& rest] (list rest))
   :proxy-method-args        (fn [& rest] (vec rest))
   :proxy-super-args         (fn [& rest] rest)
   :proxy-extend-list        (fn [& rest] (list (vec (filter #(and (not (vector? %)) (not (nil? %))) rest)) (vec (filter #(vector? %) rest))))


   :class-id-list            (fn [& rest] (vec rest))
   :class-member-id-def      ogu-class-member-id-def
   :type-def                 ogu-type-def

   :trait-method-impl        (fn [& rest] rest)
   :trait-method-impl-args   (fn [& rest] (vec rest))

   :extension                (fn [& rest] (cons 'extend-type rest))

   :recur                    (fn [& rest] (cons 'recur rest))

   :rest-args                (fn [args] (cons '& [args]))

   :trait-def                (fn [& rest] (cons 'defprotocol rest))
   :trait-method             (fn [& rest] rest)
   :trait-method-args        (fn [& rest] (vec rest))

   :eq-args                  (fn [& rest] (vec (flatten rest)))
   :def-args                 ogu-def-args
   :definition               ogu-definition
   :val-def                  (fn [& rest] (cons 'def rest))

   :argless-func-call        (fn [& rest] rest)


   :module-expr              (fn [& rest] rest)

   :module                   (fn [& rest] (str (apply str (string/join \newline rest))))})


(defn classify-form [[p form]]
      (cond
        (symbol? form) {(str "expr-" (ogu/md5 (str (ogu/uuid)))) {:form form :pos p}}
        :else (if (or (= (first form) 'def) (= (first form) 'defn))
                {(str "defv-" (second form)) {:form form :pos p}}
                {(str "expr-" (ogu/md5 (str (ogu/uuid)))) {:form form :pos p}})))

;check if a list of
(defn check-all-args-are-symbol [args] (every? symbol? args))

; check if a list of forms has a pattern matching like form
(defn is-pattern-matching? [forms]
      (loop [result false head (first forms) tail (rest forms)]
            (if (empty? tail)
              (or result (not (every? symbol? (first head))))
              (recur (or result (not (every? symbol? (first head)))) (first tail) (rest tail)))))


;; generate a sequence of ids
(def gen-ids (for [i (iterate inc 1)] (symbol (str \a i))))

(defn make-match-args [fun-body]
      (let [args (ffirst fun-body)]
           (vec (take (count args) gen-ids))))


(defn filter-for-vec-args [fun-body]
      (apply concat (for [[arg value] (partition 2 fun-body)]
                         (cond
                           (and (vector? arg) (vector? (first arg))) [[(list (first arg) :seq)] value]
                           :else [arg value]))))


(defn check-match [fun-body]
      (if-not (is-pattern-matching? fun-body)
              fun-body
              (let [args (make-match-args fun-body)]

                   (cons args (list (concat (cons 'match [args]) (filter-for-vec-args (apply concat fun-body))))))))

(defn make-variadic [forms]
      (loop [pos (java.lang.Integer/MAX_VALUE), head (first forms), tail (rest forms), name nil, result []]
            (cond
              (empty? tail) {:form (cons 'defn (cons name (check-match result))) :pos pos}
              (= :pos (first head)) (recur (min pos (second head)) (first tail) (rest tail) name result)
              :else (let [form (second head) pn (second form)]
                         (if (= 'def (first form))
                           (recur pos (first tail) (rest tail) pn (conj result (cons '[] (drop 2 form))))
                           (recur pos (first tail) (rest tail) pn (conj result (drop 2 form))))))))

(defn merge-variadic-function [fun]
      (cond
        (seq? fun) (make-variadic fun)
        :else fun))

(defn merge-functions [[k v]]
      (cond
        (string/starts-with? k "defv-") (merge-variadic-function v)
        :else v))

(defn merge-variadic [source]
      (let [tree (clojure.edn/read-string (str "[ " source "]"))
            forms (apply merge-with concat (mapv classify-form (ogu.core/zip (ogu.core/-range-to-inf 1) tree)))]
           (string/join (doall (map #(str (:form %) \newline) (sort-by :pos (map merge-functions forms)))))))

(defn transform-ast [ast]
      (merge-variadic (insta/transform ast-transformations ast)))

(def preamble "
  (require '[ogu.core :refer :all] '[clojure.core.match :refer [match]])

  ")

(defn evalue-ast [module ast]
      (let [value (transform-ast ast)]
           (load-string (str preamble value))))


(defn parse-module [options module]
      (let [arch (file module)]
           (if-not (.exists arch)
                   (println "no pudo abrir archivo: " module)
                   (let [text (str (slurp module) \newline) ast (grammar text)]
                        (if (insta/failure? ast)
                          (println "ERROR: " (insta/get-failure ast))
                          (do (when (:print options) (println ast \newline) (println (transform-ast ast)))
                              (when (:tree options) (insta/visualize (grammar text)))
                              (when (:eval options) (evalue-ast module ast))))))))