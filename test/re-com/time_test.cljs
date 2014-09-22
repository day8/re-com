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
(def new-default-min (time/TimeRecord. 0 0 0))
(def new-default-max (time/TimeRecord. 23 59 59))
(def new-alternate-min (time/TimeRecord. 6 0 0))
(def new-alternate-max (time/TimeRecord. 21 59 59))

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
    "00"       (time/display-string (time/TimeRecord. 0 nil nil))
    "06"        (time/display-string (time/TimeRecord. 6 nil nil))
    "00:00"    (time/display-string (time/TimeRecord. 0 0 nil))
    "01:30"    (time/display-string (time/TimeRecord. 1 30 nil))
    "21:59"    (time/display-string (time/TimeRecord. 21 59 nil))
    "24:30"    (time/display-string (time/TimeRecord. 24 30 nil))
    "00:00:00" (time/display-string (time/TimeRecord. 0 0 0))
    "01:30:10" (time/display-string (time/TimeRecord. 1 30 10))
    "21:59:59" (time/display-string (time/TimeRecord. 21 59 59))
    "24:30:05" (time/display-string (time/TimeRecord. 24 30 5))))

(deftest test-time-record->string
  (are [expected actual] (= expected actual)
    "0"        (time/time-record->string (time/TimeRecord. 0 nil nil))
    "6"        (time/time-record->string (time/TimeRecord. 6 nil nil))
    "11"      (time/time-record->string (time/TimeRecord. 11 nil nil))
    "000"     (time/time-record->string (time/TimeRecord. 0 0 nil))
    "0130"    (time/time-record->string (time/TimeRecord. 1 30 nil))
    "2159"    (time/time-record->string (time/TimeRecord. 21 59 nil))
    "2430"    (time/time-record->string (time/TimeRecord. 24 30 nil))
    "000"     (time/time-record->string (time/TimeRecord. 0 0 nil))))

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
  (let [tm (time/create-time-from-string "630")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      30  (:minute tm)
      nil (:second tm)))
  (let [tm (time/create-time-from-string "2252")]
    (are [expected actual] (= expected actual)
      22  (:hour tm)
      52  (:minute tm)
      nil (:second tm)))
  (let [tm (time/create-time-from-string "22:52")]
    (are [expected actual] (= expected actual)
      22  (:hour tm)
      52  (:minute tm)
      nil (:second tm)))
  (let [tm (time/create-time-from-string "6:60")] ;; Invalid values will be picked up later
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      60 (:minute tm)
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

(deftest test-validated-time-record
  (are [expected actual] (= expected actual)
    "6"       (time/time-record->string (time/validated-time-record (time/create-time :hour 6 :minute nil :second nil) new-default-min new-default-max))
    "0630"    (time/time-record->string (time/validated-time-record time-6-30 new-default-min new-default-max))
    "2359"    (time/time-record->string (time/validated-time-record (time/create-time :hour 23 :minute 59 :second nil) new-default-min new-default-max))
    ""        (time/time-record->string (time/validated-time-record (time/create-time :hour 5 :minute 0 :second 0) new-alternate-min new-alternate-max))
    ""        (time/time-record->string (time/validated-time-record (time/create-time :hour 23 :minute 59 :second nil) new-alternate-min new-alternate-max))))

(deftest test-validated-time-range
  (are [expected actual] (= expected actual)
    [6 nil nil] (time/validated-time-range [6 nil nil] default-min default-max)))
