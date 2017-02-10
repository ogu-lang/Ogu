(ns ogu-lang.parser-test
    [:require [clojure.test :refer :all]
     [ogu-lang.parser :refer :all]])

(def euler1 "test/euler/e1.ogu")
(def euler2 "test/euler/e2.ogu")
(def euler3 "test/euler/e3.ogu")
(def euler6 "test/euler/e6.ogu")
(def euler5 "test/euler/e5.ogu")
(def euler8 "test/euler/e8.ogu")


(defn eval-mod [name]
  (parse-module {:eval true} name))

(deftest test-1
  (testing "Check Euler"
    (is (= 233168 (eval-mod euler1)))
    (is (= 4613732 (eval-mod euler2)))
    (is (= 6857 (eval-mod euler3)))
    (is (= 25164150 (eval-mod euler6)))
    (is (= 232792560 (eval-mod euler5)))
    (is (= 23514624000 (eval-mod euler8)))))

(deftest test-2
  (testing "Check valid 1-10")
     (is (nil? (eval-mod "test/misc/test0.ogu")))
     (is (= 11 (eval-mod "test/misc/test1.ogu")))
     (is (= 6  (eval-mod "test/misc/test2.ogu")))
     (is (= "foobar" (eval-mod "test/misc/test3.ogu")))
     (is (= 9 (eval-mod "test/misc/test4.ogu")))
     (is (= 3999998000000 (eval-mod "test/misc/test5.ogu")))
     (is (= 8 (eval-mod "test/misc/test6.ogu")))
     (is (= 2 (eval-mod "test/misc/test7.ogu")))
     (is (= 620448401733239439360000N (eval-mod "test/misc/test8.ogu"))))
