(ns ogu-lang.parser-test
    [:require [clojure.test :refer :all]
     [ogu-lang.parser :refer :all]])

(def euler1 "test/euler/e1.ogu")
(def euler2 "test/euler/e2.ogu")

(defn eval-mod [name]
      (parse-module {:eval true} name))

(deftest test-1
         (testing "Check Euler 1"
                  (is (= 233168 (eval-mod euler1)))))


(deftest test-2
         (testing "Check Euler 2"
                  (is (= 4613732 (eval-mod euler2)))))
