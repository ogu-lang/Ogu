(ns ogu.core
    (:require [clojure.set :as s])
    (:import (java.security MessageDigest)))

(def println! println)

(def readln! read-line)

(def nothing (fn [] ))


(defn prompt! [& args]
      (binding [*print-readably* nil] (apply prn args))
      (flush)
      (read-line))


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


(defn in? [v s] (contains? (set s) v))

(defn not-in? [v s] (not (in? v s)))

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