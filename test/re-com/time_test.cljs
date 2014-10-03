(ns re-com-test.time-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [reagent.core :as reagent]
            [re-com.time :as time]))

;; --- Utility functions ---


;; --- Tests ---

(deftest test-is-valid
  (are [expected actual] (= expected actual)
    true (time/valid-time-integer? 0 0 2359)
    true (time/valid-time-integer? 600  0 2359)
    true (time/valid-time-integer? 130  0 2359)
    true (time/valid-time-integer? 2159 0 2359)
    false (time/valid-time-integer? 2430 0 2359)     ;; After max
    true  (time/valid-time-integer? 1430 600 2200)
    false (time/valid-time-integer? 430 600 2200)    ;; Before min
    false (time/valid-time-integer? 2330 600 2200))) ;; After max

(deftest test-time-from-int
  (are [expected actual] (= expected actual)
    nil (time/int-from-string nil)
    nil (time/int-from-string "")
    nil (time/int-from-string "a")
    1 (time/int-from-string "1a")
    nil (time/int-from-string "a1")
    0 (time/int-from-string "0")
    1 (time/int-from-string "1")
    59 (time/int-from-string "59")))

(deftest test-display-string
  (are [expected actual] (= expected actual)
    ""         (time/display-string [nil nil])
    "00:00"    (time/display-string [nil 0])
    "06:00"    (time/display-string [6 0])
    "00:00"    (time/display-string [0 0])
    "01:30"    (time/display-string [1 30])
    "21:59"    (time/display-string [21 59])
    "24:30"    (time/display-string [24 30])))

(deftest test-time-int->display-string
  (are [expected actual] (= expected actual)
    ""       (time/time-int->display-string nil)
    "00:00"  (time/time-int->display-string 0)
    "06:00"  (time/time-int->display-string 600)
    "11:00"  (time/time-int->display-string 1100)
    "09:00"  (time/time-int->display-string 900)
    "01:30"  (time/time-int->display-string 130)
    "21:59"  (time/time-int->display-string 2159)
    "24:30"  (time/time-int->display-string 2430)))

(deftest test-time-int->hour-minute
  (are [expected actual] (= expected actual)
    [nil nil]   (time/time-int->hour-minute nil)
    [0 0]   (time/time-int->hour-minute 0)
    [0 50]  (time/time-int->hour-minute 50)
    [1 0]   (time/time-int->hour-minute 100)
    [11 59] (time/time-int->hour-minute 1159)
    [23 59] (time/time-int->hour-minute 2359)))

(deftest test-string->time-integer
  (are [expected actual] (= expected actual)
    600   (time/string->time-integer "600")
    630   (time/string->time-integer "630")
    630   (time/string->time-integer "6:30")
    630   (time/string->time-integer "06:30")
    3000  (time/string->time-integer "30")
    2359  (time/string->time-integer "2359")
    2359  (time/string->time-integer "23:59")))

(deftest test-validate-hours
  (are [expected actual] (= expected actual)
    true  (time/validate-hours 630 0 2359)
    false (time/validate-hours 530 600 2200)
    true  (time/validate-hours nil 0 2359)))

(deftest test-validate-minutes
  (are [expected actual] (= expected actual)
    true (time/validate-minutes nil)
    true (time/validate-minutes 655)
    true (time/validate-minutes 600)
    true (time/validate-minutes 2359)
    false (time/validate-minutes 98)
    false (time/validate-minutes 2398)))

(deftest test-validated-time-integer
  (are [expected actual] (= expected actual)
    600   (time/validated-time-integer 600 0 2359)
    630   (time/validated-time-integer 630 0 2359)
    2359  (time/validated-time-integer 2359 0 2359)
    1500  (time/validated-time-integer 1575 600 2200)    ;; invalid minutes
    nil   (time/validated-time-integer 500 600 2200)     ;; Too early
    nil   (time/validated-time-integer 2315 600 2200)))  ;; Too late

(deftest test-validate-time-range
  (are [expected actual] (= expected actual)
    true     (time/validate-time-range 630 0 2359)
    false    (time/validate-time-range 2430 0 2359)
    false    (time/validate-time-range 500 600 2200)
    false    (time/validate-time-range 2345 600 2200)))

(deftest test-atom-on
  (let [mdl (time/atom-on 123 nil)]
    (is (satisfies? cljs.core/IDeref mdl) "Expected an atom.")
    (is (= @mdl 123) "Expected value to be 123."))
  (let [mdl (time/atom-on (reagent/atom 123) 456)]
    (is (satisfies? cljs.core/IDeref mdl) "Expected an atom.")
    (is (= @mdl 123) "Expected value to be 123."))
  (let [mdl (time/atom-on nil 456)]
    (is (satisfies? cljs.core/IDeref mdl) "Expected an atom.")
    (is (= @mdl 456) "Expected value to be 456.")))

(deftest test-time-input
 (is (fn? (time/time-input :model 1500)) "Expected a function.")
 (let [result  ((time/time-input :model 1500))]
   (is (= :span.input-append.bootstrap-timepicker (first result)) "Expected first element to be :span.input-append.bootstrap-timepicker")
   (let [time-input (last result)
         time-input-attrs (nth time-input 1)]
     (is (= :input.input-small (first time-input)) "Expected time input start with :input.input-small")
     (are [expected actual] (= expected actual)
       false         (:disabled time-input-attrs)
       "15:00"       (:value time-input-attrs)
       "text"        (:type time-input-attrs)
       "time-entry"  (:class time-input-attrs)
       true     (fn? (:on-focus time-input-attrs))
       true     (fn? (:on-blur time-input-attrs))
       true     (fn? (:on-change time-input-attrs))
       true     (fn? (:on-mouse-up time-input-attrs)))))
 (is (fn? (time/time-input :model 1500 :minimum 600 :maximum 2159)) "Expected a function.")
 (is (thrown? js/Error (time/time-input :model "abc") "should fail - model is invalid"))
 (is (thrown? js/Error (time/time-input :model 930 :minimum "abc" :maximum 2159) "should fail - minimum is invalid"))
 (is (thrown? js/Error (time/time-input :model 930 :minimum 600 :maximum "fred") "should fail - maximum is invalid"))
 (is (thrown? js/Error (time/time-input :model 530 :minimum 600 :maximum 2159) "should fail - model is before range start"))
 (is (thrown? js/Error (time/time-input :model 2230 :minimum 600 :maximum 2159) "should fail - model is after range end")))

