(ns ogu-lang.parser-test
    [:require [clojure.test :refer :all]
     [ogu-lang.parser :refer :all]])

(def euler1 "test/euler/e1.ogu")
(def euler2 "test/euler/e2.ogu")
(def euler6 "test/euler/e6.ogu")
(def euler8 "test/euler/e8.ogu")


(defn eval-mod [name]
  (parse-module {:eval true} name))

(deftest test-1
  (testing "Check Euler"
    (is (= 233168 (eval-mod euler1)))
    (is (= 4613732 (eval-mod euler2)))
    (is (= 25164150 (eval-mod euler6)))
    (is (= 23514624000 (eval-mod euler8)))))


