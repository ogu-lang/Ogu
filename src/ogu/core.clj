(ns ogu.core
  (:require [clojure.set :as s] ))

(defn println! [& args]
  (binding [*print-readably* nil] (apply prn args)))

(defn  sum [args] (reduce + (seq args)))

(defn union [a b] (s/union (set a) (set b)))

(defn -range-to-inf
      ([start] (iterate inc' start))
      ([start, step] (iterate #(+' % step) start)))

(def tail rest)

(def head first)

(def boolean-array! boolean-array)

(def aset! aset)