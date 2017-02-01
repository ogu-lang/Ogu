(ns ogu.core
  (:require [clojure.set :as s] ))

(defn println! [& args]
  (binding [*print-readably* nil] (apply prn args)))

(defn  sum [args] (reduce + (seq args)))

(defn union [a b] (s/union (set a) (set b)))