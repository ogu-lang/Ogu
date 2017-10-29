(ns ogu.core (:gen-class)
    (:require [clojure.set :as s])
    (:import (java.security MessageDigest)))

(def ^:dynamic **args** [])

(def println! println)

(def readln! read-line)

(def nothing (fn [] ))

(def typeof type)

(def classof class)

(def curry partial)

(def reader clojure.java.io/reader)

(def file clojure.java.io/file)

(defn split-words [s]
      (let [w (clojure.string/trim s)]
           (if (empty? w)
             []
             (clojure.string/split w #"\s+"))))

(defn isa-type? [t obj]
      (cond
        (instance? clojure.lang.PersistentArrayMap t) (satisfies? t obj)
        :else (instance? t obj)))


(defn prompt! [& args]
      (binding [*print-readably* nil] (apply pr args))
      (flush)
      (read-line))

; try-parse
(defn to-int [s d]
      (try
        (Integer/parseInt s)
        (catch java.lang.NumberFormatException e d)))

(defn  sum [args] (reduce + (seq args)))

(defn union [a b] (s/union (set a) (set b)))

(defn -range-to-inf
      ([start] (iterate inc' start))
      ([start, step] (iterate #(+' % step) start)))

(defn not-empty? [coll] (seq coll))

(def tail rest)

(def to-set set)

(def head first)

(def fst first)

(def snd second)

(defn zip [a b] (map vector a b))

(def uniq distinct)

(defn is-digit? [d] (Character/isDigit d))

(defn elem [v s] (contains? (set s) v))

(defn not-elem [v s] (not (elem v s)))

(defn member? [v s] (contains? (set s) v))

(defn not-member? [v s] (not (member? v s)))

(defn to-digit
      ([d] (Character/digit d 10))
      ([d b] (Character/digit d b)))

(def boolean-array! boolean-array)

(def aset! aset)

(def length count)

(def trim clojure.string/trim)

(def upper clojure.string/upper-case)

(def lower clojure.string/lower-case)

(def join clojure.string/join)

;; from here are fragments of numeric-tower

(def ^{:private true} minus (first [-' -]))
(def ^{:private true} mult (first [*' *]))

;; feature testing macro, based on suggestion from Chas Emerick:
(defmacro when-available
          [sym & body]
          (try
            (when (resolve sym)
                  (list* 'do body))
            (catch ClassNotFoundException _#)))

(defn- expt-int [base pow]
       (loop [n pow, y (num 1), z base]
             (let [t (even? n), n (quot n 2)]
                  (cond
                    t (recur n y (mult z z))
                    (zero? n) (mult z y)
                    :else (recur n (mult z y) (mult z z))))))

(defn pow
      "(pow base power) is base to the  power.
    Returns an exact number if the base is an exact number and the power is an integer, otherwise returns a double."
      [base power]
      (if (and (not (float? base)) (integer? power))
        (cond
          (pos? power) (expt-int base power)
          (zero? power) (cond
                        (= (type base) BigDecimal) 1M
                        (= (type base) java.math.BigInteger) (java.math.BigInteger. "1")
                        (when-available clojure.lang.BigInt (= (type base) clojure.lang.BigInt))
                        (when-available clojure.lang.BigInt (bigint 1))
                        :else 1)
          :else (/ 1 (expt-int base (minus power))))
        (Math/pow base power)))

(def pi Math/PI)

(defn md5 [s]
      (let [algorithm (MessageDigest/getInstance "MD5")
            raw (.digest algorithm (.getBytes s))]
           (format "%032x" (BigInteger. 1 raw))))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defmacro -def-ogu-var- [name value]
  (let [meta-name (with-meta name  {:dynamic true}) ]
    `(def ~meta-name ~value)))

(defmacro -def-ogu-type- [name fields  & opts+specs]
  (let [filter-fields  (vec (map #(if (vector? %) (with-meta (second %) {:volatile-mutable true})  %) fields))  ]
    `(deftype ~name ~filter-fields  ~@opts+specs)))

; based on this code https://github.com/richhickey/clojure-contrib/blob/master/src/main/clojure/clojure/contrib/import_static.clj

(defmacro import-static
          "Imports the named static fields and/or static methods of the class
          as (private) symbols in the current namespace.
          Example:
              user=> (import-static java.lang.Math PI sqrt)
              nil
              user=> PI
              3.141592653589793
              user=> (sqrt 16)
              4.0
          Note: The class name must be fully qualified, even if it has already
          been imported.  Static methods are defined as MACROS, not
          first-class fns."
          [class & fields-and-methods]
          (let [only (set (map str fields-and-methods))
                the-class (. Class forName (str class))
                static? (fn [x]
                            (. java.lang.reflect.Modifier
                               (isStatic (. x (getModifiers)))))
                statics (fn [array]
                            (set (map (memfn getName)
                                      (filter static? array))))
                all-fields (statics (. the-class (getFields)))
                all-methods (statics (. the-class (getMethods)))
                fields-to-do (s/intersection all-fields only)
                methods-to-do (s/intersection all-methods only)
                make-sym (fn [string]
                             (with-meta (symbol string) {:private true}))
                import-field (fn [name]
                                 (list 'def (make-sym name)
                                       (list '. class (symbol name))))
                import-method (fn [name]
                                  (list 'defmacro (make-sym name)
                                        '[& args]
                                        (list 'list ''. (list 'quote class)
                                              (list 'apply 'list
                                                    (list 'quote (symbol name))
                                                    'args))))]
               `(do ~@(map import-field fields-to-do)
                    ~@(map import-method methods-to-do))))


; based on << macro
; defined here: https://github.com/clojure/core.incubator/blob/master/src/main/clojure/clojure/core/strint.clj

(defn- silent-read
       "Attempts to clojure.core/read a single form from the provided String, returning
       a vector containing the read form and a String containing the unread remainder
       of the provided String. Returns nil if no valid form can be read from the
       head of the String."
       [s]
       (try
         (let [r (-> s java.io.StringReader. java.io.PushbackReader.)]
              [(read r) (slurp r)])
         (catch Exception e))) ; this indicates an invalid form -- the head of s is just string data

(defn- interpolate
       "Yields a seq of Strings and read forms."
       ([s atom?]
         (lazy-seq
           (if-let [[form rest] (silent-read (subs s (if atom? 2 1)))]
                   (cons form (interpolate (if atom? (subs rest 1) rest)))
                   (cons (subs s 0 2) (interpolate (subs s 2))))))
       ([^String s]
         (if-let [start (->> ["${"]
                             (map #(.indexOf s ^String %))
                             (remove #(== -1 %))
                             sort
                             first)]
                 (lazy-seq (cons
                             (subs s 0 start)
                             (interpolate (subs s start) (= \{ (.charAt s (inc start))))))
                 [s])))

(defmacro fmt
          "Limited string interpolation, just ${var}"
          [& strings]
          `(str ~@(interpolate (apply str strings))))

(defn func-fmt [s]
      (println "func-fmt " s)
      (interpolate s))

(defn adt-name
  [obj]
  (-> obj meta :adt))

(defmacro data
  "Declare a sum type with a set of constructors
   (data Tree
     (Branch left right)
     (Leaf data))
   Automatically generates classes for each variant using `deftype` with the same field names, as
   well as providing an implementation of `IMatchLookup` for use with core.match."
  [ty & variants]
  (let [defns
        (for [variant variants]
          (let [[ctor & fields] variant
                field-kvs (mapcat (fn [f] [(keyword (name f)) f]) fields)]
            `(deftype ~ctor [~@fields]
               clojure.core.match.protocols/IMatchLookup
               (clojure.core.match.protocols/val-at [this# k# not-found#]
                 (case k#
                   ~@field-kvs
                   :type (type this#)
                   not-found#)))))]
    `(do ~@defns)))