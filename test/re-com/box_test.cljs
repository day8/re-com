(ns re-com-test.box-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [reagent.core :as reagent]
            [re-com.box :refer [flex-child-style]]))

(deftest test-flex-child-style
         (are [expected actual] (= expected actual)
              "initial"   (:flex (flex-child-style "initial"))
              "auto"      (:flex (flex-child-style "auto"))
              "none"      (:flex (flex-child-style "none"))
              "0 0 100px" (:flex (flex-child-style "100px"))
              "0 0 4.5em" (:flex (flex-child-style "4.5em"))
              "60 1 0px"  (:flex (flex-child-style "60%"))
              "60 1 0px"  (:flex (flex-child-style "60"))
              "5 4 0%"    (:flex (flex-child-style "5 4 0%"))))
