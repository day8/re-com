(ns re-com-test.time-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [reagent.core :as reagent]
            [re-com.time :as time]))

(def default-min (time/TimeRecord. 0 0))
(def default-max (time/TimeRecord. 23 59))
(def alternate-min (time/TimeRecord. 6 0))
(def alternate-max (time/TimeRecord. 21 59))

(def time-6-30 (time/create-time :hour 6 :minute 30))
(def time-5-30 (time/create-time :hour 5 :minute 30))
(def time-23-59-59 (time/create-time :hour 23 :minute 59))

(deftest test-is-valid
  (are [expected actual] (= expected actual)
    true (time/valid-time? (time/TimeRecord. 0 nil) default-min default-max)
    true (time/valid-time? (time/TimeRecord. 6 nil)  default-min default-max)
    true (time/valid-time? (time/TimeRecord. 0 0)   default-min default-max)
    true (time/valid-time? (time/TimeRecord. 1 30)  default-min default-max)
    true (time/valid-time? (time/TimeRecord. 21 59) default-min default-max)
    false (time/valid-time? (time/TimeRecord. 24 30) default-min default-max)
    true  (time/valid-time? (time/TimeRecord. 14 30) alternate-min alternate-max)
    false (time/valid-time? (time/TimeRecord. 4 30) alternate-min alternate-max)    ;; Before min
    false (time/valid-time? (time/TimeRecord. 23 30) alternate-min alternate-max))) ;; After max

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

(deftest test-time-from-int
  (are [expected actual] (= expected actual)
    "00:00" (time/display-string (time/time-from-int 0))
    "00:59" (time/display-string (time/time-from-int 59))
    "09:55" (time/display-string (time/time-from-int 955))
    "23:59" (time/display-string (time/time-from-int 2359))))

(deftest test-time-record->int
  (are [expected actual] (= expected actual)
    0    (time/time-record->int (time/TimeRecord. nil nil))
    0    (time/time-record->int (time/TimeRecord. 0 nil))
    59   (time/time-record->int (time/TimeRecord. 0 59))
    955  (time/time-record->int (time/TimeRecord. 9 55))
    2359 (time/time-record->int (time/TimeRecord. 23 59))))

(deftest test-display-string
  (are [expected actual] (= expected actual)
    "00:00"    (time/display-string (time/TimeRecord. nil nil))
    "00:00"    (time/display-string (time/TimeRecord. 0 nil))
    "06:00"    (time/display-string (time/TimeRecord. 6 nil))
    "00:00"    (time/display-string (time/TimeRecord. 0 0))
    "01:30"    (time/display-string (time/TimeRecord. 1 30))
    "21:59"    (time/display-string (time/TimeRecord. 21 59))
    "24:30"    (time/display-string (time/TimeRecord. 24 30))))

(deftest test-time-record->string
  (are [expected actual] (= expected actual)
    "00"      (time/time-record->string (time/TimeRecord. 0 nil))
    "06"      (time/time-record->string (time/TimeRecord. 6 nil))
    "11"      (time/time-record->string (time/TimeRecord. 11 nil))
    "0900"    (time/time-record->string (time/TimeRecord. 9 0))
    "0130"    (time/time-record->string (time/TimeRecord. 1 30))
    "2159"    (time/time-record->string (time/TimeRecord. 21 59))
    "2430"    (time/time-record->string (time/TimeRecord. 24 30))
    "0000"    (time/time-record->string (time/TimeRecord. 0 0))))

(deftest test-create-time
  (let [tm (time/create-time :hour 23 :minute 30)]
    (are [expected actual] (= expected actual)
      23 (:hour tm)
      30 (:minute tm)))
  (let [tm (time/create-time :hour 23 :minute 30)]
    (are [expected actual] (= expected actual)
      23 (:hour tm)
      30 (:minute tm))))

(deftest test-create-time-from-map
  (let [tm (time/create-time-from-map{:hour 23 :minute 45})]
    (are [expected actual] (= expected actual)
      23 (:hour tm)
      45 (:minute tm))))

(deftest test-create-time-from-string
  (let [tm (time/create-time-from-string "6")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      nil (:minute tm)))
  (let [tm (time/create-time-from-string "6:30")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      30  (:minute tm)))
  (let [tm (time/create-time-from-string "630")]
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      30  (:minute tm)))
  (let [tm (time/create-time-from-string "2252")]
    (are [expected actual] (= expected actual)
      22  (:hour tm)
      52  (:minute tm)))
  (let [tm (time/create-time-from-string "22:52")]
    (are [expected actual] (= expected actual)
      22  (:hour tm)
      52  (:minute tm)))
  (let [tm (time/create-time-from-string "6:60")] ;; Invalid values will be picked up later
    (are [expected actual] (= expected actual)
      6   (:hour tm)
      60 (:minute tm))))

(deftest test-validate-hours
  (are [expected actual] (= expected actual)
    true  (time/validate-hours time-6-30 default-min default-max)
    false (time/validate-hours (time/create-time :hour 5 :minute 30) alternate-min alternate-max)
    true  (time/validate-hours nil default-min default-max)))

(deftest test-validate-minutes
  (are [expected actual] (= expected actual)
    true (time/validate-minutes (time/create-time :hour 6 :minute nil))
    true (time/validate-minutes (time/create-time :hour 6 :minute 55))
    true (time/validate-minutes (time/create-time :hour 6 :minute 0))))

(deftest test-validated-time-record
  (are [expected actual] (= expected actual)
    "06"      (time/time-record->string (time/validated-time-record (time/create-time :hour 6 :minute nil) default-min default-max))
    "0630"    (time/time-record->string (time/validated-time-record time-6-30 default-min default-max))
    "2359"    (time/time-record->string (time/validated-time-record (time/create-time :hour 23 :minute 59) default-min default-max))
    ""        (time/time-record->string (time/validated-time-record (time/create-time :hour 5 :minute 0) alternate-min alternate-max))
    ""        (time/time-record->string (time/validated-time-record (time/create-time :hour 23 :minute 59) alternate-min alternate-max))))

(deftest test-validate-time-range
  (are [expected actual] (= expected actual)
    true     (time/validate-time-range (time/create-time :hour 6 :minute 30) default-min default-max)
    false    (time/validate-time-range (time/create-time :hour 24 :minute 30) default-min default-max)
    false    (time/validate-time-range (time/create-time :hour 5 :minute 0) alternate-min alternate-max)
    false    (time/validate-time-range (time/create-time :hour 23 :minute 45) alternate-min alternate-max)))

