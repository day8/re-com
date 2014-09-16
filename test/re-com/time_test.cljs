(ns re-comp-test.time-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [reagent.core :as reagent]
            [re-com.time :as time]))

(def default-min [0 0])
(def default-max [23 59])
(def alternate-min [6 0])
(def alternate-max [21 59])

(deftest test-first-char
  (are [expected actual] (= expected actual)
    "1"   (time/first-char "1" default-min default-max)   ;; Valid character - should be returned unchanged
    ""    (time/first-char "a" default-min default-max)    ;; Expected invalid character to be ignored.
    "08" (time/first-char "8" default-min default-max)  ;; Number exceeds default mimimum - should be treated as second digit of hour.
    ""   (time/first-char "5" alternate-min alternate-max)))   ;; Invalid character - outside time range

(deftest test-second-char
  (are [expected actual] (= expected actual)
    "11"   (time/second-char "11" default-min default-max)   ;; Valid character - should be returned unchanged
    "01"   (time/second-char "01" default-min default-max)   ;; Valid character - should be returned unchanged
    "1"    (time/second-char "1a" default-min default-max)   ;; Expected invalid character to be ignored.
    "2"    (time/second-char "25" default-min default-max)   ;; Number exceeds default mimimum - second digit should be ignored.
    "0"   (time/second-char  "05" alternate-min alternate-max)   ;; Invalid character - outside time range
    "06"   (time/second-char  "06" alternate-min alternate-max)   ;; Within time range
    "2"   (time/second-char  "22" alternate-min alternate-max))) ;; Outside range

(deftest test-third-char
  (are [expected actual] (= expected actual)
    "11:"   (time/third-char "11:" default-min default-max)   ;; Valid character - should be returned unchanged except for added colon
    "11:"   (time/third-char "11-" default-min default-max)   ;; Valid character - should be changed to a colon except for added colon
    "06:3"  (time/third-char "063" default-min default-max)   ;; Valid character - should be returned unchanged except for added colon
    "11:"   (time/third-char "11a" default-min default-max)   ;; Expected invalid character to be ignored and colon added.
    "23:"   (time/third-char "236" default-min default-max))) ;; Number exceeds number of minutes that are possible - third digit should be ignored and colon added.

(deftest test-fourth-char
  (are [expected actual] (= expected actual)
    "11:3"   (time/fourth-char "11:3" default-min default-max)   ;; Valid character - should be returned unchanged
    "01:0"   (time/fourth-char "01:0" default-min default-max)   ;; Valid character - should be returned unchanged
    "11:"    (time/fourth-char "11::" default-min default-max)   ;; Expected additional colon to be ignored.
    "11:"    (time/fourth-char "11:a" default-min default-max)   ;; Expected invalid character to be ignored.
    "22:"    (time/fourth-char "22:6" default-min default-max))) ;; Number exceeds number of minutes that are possible - third digit should be ignored and colon added.

(deftest test-fifth-char
  (are [expected actual] (= expected actual)
    "11:30"   (time/fifth-char "11:30" default-min default-max)   ;; Valid character - should be returned unchanged
    "01:00"   (time/fifth-char "01:00" default-min default-max)   ;; Valid character - should be returned unchanged
    "11:0"    (time/fifth-char "11:0a" default-min default-max)))   ;; Expected invalid character to be ignored.

(deftest test-validate-each-character
   (let [tmp-model (reagent/atom "22:30")
         _ (time/validate-each-character tmp-model default-min default-max)]
     (is (= "22:30" @tmp-model))
     (reset! tmp-model "29:30")  ;; Exceeds max
     (time/validate-each-character tmp-model default-min default-max)
     (is (= "2" @tmp-model))))

(deftest test-validate-hours
  (are [expected actual] (= expected actual)
    false (time/validate-hours "aa" default-min default-max)   ;; Invalid character - not valid
    true (time/validate-hours "01" default-min default-max)
    true (time/validate-hours "22" default-min default-max)
    true (time/validate-hours "18" alternate-min alternate-max)
    true (time/validate-hours "25" alternate-min alternate-max))) ;; Even though it is after the max

(deftest test-validate-third-char
  (are [expected actual] (= expected actual)
    false (time/validate-third-char \a default-min default-max)   ;; Not valid
    false (time/validate-third-char \- default-min default-max)   ;; Not valid
    true  (time/validate-third-char \: default-min default-max)))

(deftest test-validate-minutes
  (are [expected actual] (= expected actual)
    false (time/validate-minutes "aa" default-min default-max)   ;; Invalid character - not valid
    true (time/validate-minutes "55" default-min default-max)
    true (time/validate-minutes "00" alternate-min alternate-max)
    false (time/validate-minutes "65" alternate-min alternate-max))) ;; After max minutes in an hour

(deftest test-is-valid
  (are [expected actual] (= expected actual)
    false (time/is-valid (reagent/atom nil) default-min default-max)
    false (time/is-valid (reagent/atom "") default-min default-max)
    false (time/is-valid (reagent/atom "1") default-min default-max)
    false (time/is-valid (reagent/atom "04") default-min default-max)
    false (time/is-valid (reagent/atom "04:") default-min default-max)
    false (time/is-valid (reagent/atom "04:3") default-min default-max)
    true  (time/is-valid (reagent/atom "04:30") default-min default-max)
    false (time/is-valid (reagent/atom "24:30") default-min default-max)       ;; After max
    true  (time/is-valid (reagent/atom "14:30") alternate-min alternate-max)
    false (time/is-valid (reagent/atom "04:30") alternate-min alternate-max)   ;; Before min
    false (time/is-valid (reagent/atom "23:15") alternate-min alternate-max))) ;; After max

(deftest test-model-str
  (are [expected actual] (= expected actual)
    "00:00" (time/model-str (reagent/atom [0 0]))
    "01:30" (time/model-str (reagent/atom [1 30]))
    "21:59" (time/model-str (reagent/atom [21 59]))
    "24:30" (time/model-str (reagent/atom [24 30]))))
