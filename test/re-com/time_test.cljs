(ns re-comp-test.time-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [reagent.core :as reagent]
            [re-com.time :as time]))

(def default-min [0 0])
(def default-max [23 59])
(def alternate-min [6 0])
(def alternate-max [21 59])
;; TODO remove previous definitions
(def new-default-min (time/TimeVector. 0 0 0))
(def new-default-max (time/TimeVector. 23 59 59))
(def new-alternate-min (time/TimeVector. 6 0 0))
(def new-alternate-max (time/TimeVector. 21 59 59))

(def time-6-30 (time/create-time :hour 6 :minute 30 :second nil))
(def time-5-30 (time/create-time :hour 5 :minute 30 :second 0))
(def time-23-59-59 (time/create-time :hour 23 :minute 59 :second 59))

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

(deftest test-validate-hours-string-string
  (are [expected actual] (= expected actual)
    false (time/validate-hours-string "aa" default-min default-max)   ;; Invalid character - not valid
    true (time/validate-hours-string "01" default-min default-max)
    true (time/validate-hours-string "22" default-min default-max)
    true (time/validate-hours-string "18" alternate-min alternate-max)
    true (time/validate-hours-string "25" alternate-min alternate-max))) ;; Even though it is after the max

(deftest test-validate-third-char
  (are [expected actual] (= expected actual)
    false (time/validate-third-char \a default-min default-max)   ;; Not valid
    false (time/validate-third-char \- default-min default-max)   ;; Not valid
    true  (time/validate-third-char \: default-min default-max)))

(deftest test-validate-minutes-string
  (are [expected actual] (= expected actual)
    false (time/validate-minutes-string "aa" default-min default-max)   ;; Invalid character - not valid
    true (time/validate-minutes-string "55" default-min default-max)
    true (time/validate-minutes-string "00" alternate-min alternate-max)
    false (time/validate-minutes-string "65" alternate-min alternate-max))) ;; After max minutes in an hour

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

;;-------------------------------------------------------------------

(deftest test-int-from-string
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
    "00"       (time/display-string (time/TimeVector. 0 nil nil))
    "06"        (time/display-string (time/TimeVector. 6 nil nil))
    "00:00"    (time/display-string (time/TimeVector. 0 0 nil))
    "01:30"    (time/display-string (time/TimeVector. 1 30 nil))
    "21:59"    (time/display-string (time/TimeVector. 21 59 nil))
    "24:30"    (time/display-string (time/TimeVector. 24 30 nil))
    "00:00:00" (time/display-string (time/TimeVector. 0 0 0))
    "01:30:10" (time/display-string (time/TimeVector. 1 30 10))
    "21:59:59" (time/display-string (time/TimeVector. 21 59 59))
    "24:30:05" (time/display-string (time/TimeVector. 24 30 5))))

(deftest test-create-time
  (let [tm (time/create-time :hour 23 :minute 30 :second nil)]
    (are [expected actual] (= expected actual)
      23 (:hour tm)
      30 (:minute tm))
    (is (nil? (:second tm))))
  (let [tm (time/create-time :hour 23 :minute 30 :second 59)]
    (are [expected actual] (= expected actual)
    23 (:hour time-23-59-59)
    59 (:minute time-23-59-59)
    59 (:second time-23-59-59))))

(deftest test-string-as-model-values
  (let [vals (time/string-as-model-values "6")]
    (is (= (count vals) 3)  "Expected a 3 element vector")
    (is (= (first vals) 6)  "Expected hours value to be 6")
    (is (nil? (nth vals 1)) "Expected minutes value to be nil")
    (is (nil? (last vals))  "Expected seconds value to be nil"))
  (let [vals (time/string-as-model-values "06:30")]
    (is (= (count vals) 3)  "Expected a 3 element vector")
    (is (= (first vals) 6)  "Expected hours value to be 6")
    (is (= (nth vals 1) 30) "Expected minutes value to be 30")
    (is (nil? (last vals))  "Expected seconds value to be nil"))
    (let [vals (time/string-as-model-values "06:30:25")]
    (is (= (count vals) 3)  "Expected a 3 element vector")
    (is (= (first vals) 6)  "Expected hours value to be 6")
    (is (= (nth vals 1) 30) "Expected minutes value to be 30")
    (is (= (last vals) 25)  "Expected seconds value to be 25")))

(deftest test-create-time-from-string
  (let [tm (time/create-time-from-string "6")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      nil (:minute tm)
      nil (:second tm)))
  (let [tm (time/create-time-from-string "6:30")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      30  (:minute tm)
      nil (:second tm)))
      (let [tm (time/create-time-from-string "6:30:55")]
  (are [expected actual] (= expected actual)
      6  (:hour tm)
      30 (:minute tm)
      55 (:second tm)))
  (let [tm (time/create-time-from-string "6pm")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      nil (:minute tm)
      nil (:second tm))))

(deftest test-vector->seconds
  (are [expected actual] (= expected actual)
    0     (time/vector->seconds [nil nil nil])
    21600 (time/vector->seconds [6 nil nil])
    21600 (time/vector->seconds [6 0 0])
    23400 (time/vector->seconds [6 30 0])))

(deftest test-validate-hours
  (are [expected actual] (= expected actual)
    true  (time/validate-hours time-6-30 new-default-min new-default-max)
    false (time/validate-hours (time/create-time :hour 5 :minute 30 :second 0) new-alternate-min new-alternate-max)
    true  (time/validate-hours nil new-default-min new-default-max)))

(deftest test-validate-minutes
  (are [expected actual] (= expected actual)
    true (time/validate-minutes (time/create-time :hour 6 :minute nil :second nil))
    true (time/validate-minutes (time/create-time :hour 6 :minute 55 :second nil))
    true (time/validate-minutes (time/create-time :hour 6 :minute 0 :second nil))))

(deftest test-validated-time-vector
  (are [expected actual] (= expected actual)
    "06"       (time/time-vector->string (time/validated-time-vector (time/create-time :hour 6 :minute nil :second nil) new-default-min new-default-max))
    "06:30"    (time/time-vector->string (time/validated-time-vector time-6-30 new-default-min new-default-max))
    "06:30:55" (time/time-vector->string (time/validated-time-vector (time/create-time :hour 6 :minute 30 :second 55) new-default-min new-default-max))
    "23:59:59" (time/time-vector->string (time/validated-time-vector (time/create-time :hour 23 :minute 59 :second 59) new-default-min new-default-max))
    "00"       (time/time-vector->string (time/validated-time-vector (time/create-time :hour 5 :minute 0 :second 0) new-alternate-min new-alternate-max))
    "00"       (time/time-vector->string (time/validated-time-vector (time/create-time :hour 23 :minute 59 :second 59) new-alternate-min new-alternate-max))))

(deftest test-validated-time-range
  (are [expected actual] (= expected actual)
    [6 nil nil] (time/validated-time-range [6 nil nil] default-min default-max)))
