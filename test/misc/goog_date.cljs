;; Exercise various assumptions about local vs utc based goog.date conversion.
;; These tests assume they are being run in a timezone where the timezone offset is not 0
;; otherwise some assertions will fail. To test against alternate timezones on Linux system you can
;; run a special browser session where a differemt timezone can be simulated without
;; efecting the system. e.g. the following will run using the New Zealand timezone.
;; See for others https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
;; `TZ=NZ google-chrome` or `TZ=NZ firefox`
;; Make sure you don't have an existing browser window open for the above to take effect.
(ns test.misc.goog-date
  (:require
    [goog.date.Date]
    [goog.date.DateTime]
    [goog.date.UtcDateTime]
    [cemerick.cljs.test :refer-macros [is deftest testing]]))

(let [local-date (js/goog.date.DateTime.)]
  (.log js/console (str "local date/time: "
    (.getYear local-date) "/" (inc (.getMonth local-date)) "/" (.getDate local-date)
    "-" (.getHours local-date) ":" (.getMinutes local-date) ":" (.getSeconds local-date)
    " timezoneOffset: " (.getTimezoneOffset local-date))))


(deftest test:Date_from_epoch_ms
  (let [local-date (doto (js/goog.date.Date.) (.setTime 1431216000000))] ; Midnight Sun, May 10, 2015
    (is (= 1431216000000 (.getTime local-date)) "underlying epoch milliseconds should be kept from constructor.")
    (is (= 0  (.getWeekday local-date)) "Expecting Sunday")
    (is (= 0  (.getUTCWeekday local-date)) "Expecting Sunday")
    (is (= 10 (.getDate local-date)))
    (is (= 10 (.getUTCDate local-date)))
    (is (= 4  (.getMonth local-date)) "expecting April")
    (is (= 4  (.getUTCMonth local-date)) "expecting April")
    (is (= 2015 (.getYear local-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear local-date)) "expecting 2015")
    (is (= 0 (.getUTCHours local-date)) "hours should be zero") ; notice .setTime abides by whatever hours
    (is (not= 0 (.getTimezoneOffset local-date)) "timezone offset should not be 0")))


(deftest test:Date_from_yymmdd
  (let [local-date (js/goog.date.Date. 2015 4 10)] ; Midnight Sun, May 10, 2015
    (is (= 0 (.getWeekday local-date)) "Expecting Sunday")
    (is (not= 0 (.getUTCWeekday local-date)) "UTC weekday should be different")
    (is (= 10 (.getDate local-date)))
    (is (not= 10 (.getUTCDate local-date)) "UTC date should be different")
    (is (= 4 (.getMonth local-date)) "expecting April")
    (is (= 4 (.getUTCMonth local-date)) "expecting April")
    (is (= 2015 (.getYear local-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear local-date)) "expecting 2015")
    (is (not= 0 (.getUTCHours local-date)) "suprisingly it answers") ; notice this is different to using .setTime
    (is (not= 0 (.getTimezoneOffset local-date)) "timezone offset should not be 0")))


(deftest test:DateTime_from_epoch_ms
  (let [local-date (doto (js/goog.date.DateTime.) (.setTime 1431216000000))] ; Midnight Sun, May 10, 2015
    (is (= 1431216000000 (.getTime local-date)) "underlying epoch milliseconds should be kept from constructor.")
    (is (= 0 (.getWeekday local-date)) "Expecting Sunday")
    (is (= 0 (.getUTCWeekday local-date)) "Expecting Sunday")
    (is (= 10 (.getDate local-date)))
    (is (= 10 (.getUTCDate local-date)))
    (is (= 4 (.getMonth local-date)) "expecting April")
    (is (= 4 (.getUTCMonth local-date)) "expecting April")
    (is (= 2015 (.getYear local-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear local-date)) "expecting 2015")
    (is (= 0 (.getUTCHours local-date)) "hours should be zero") ; notice .setTime abides by whatever hours
    (is (= 0 (.getUTCHours local-date)))
    (is (= 0 (.getMinutes local-date)))
    (is (= 0 (.getUTCMinutes local-date)))
    (is (= 0 (.getSeconds local-date)))
    (is (= 0 (.getUTCSeconds local-date)))
    (is (= 0 (.getUTCMilliseconds local-date)))
    (is (not= 0 (.getTimezoneOffset local-date)) "timezone offset should not be 0")))


(deftest test:DateTime_from_yymmdd
  (let [local-date (js/goog.date.DateTime. 2015 4 10 0 0 0 0)] ; Midnight Sun, May 10, 2015
    (is (= 0 (.getWeekday local-date)) "Expecting Sunday")
    (is (not= 0 (.getUTCWeekday local-date)) "UTC weekday should be different")
    (is (= 10 (.getDate local-date)))
    (is (not= 10 (.getUTCDate local-date)) "UTC date should be different")
    (is (= 4 (.getMonth local-date)) "expecting April")
    (is (= 4 (.getUTCMonth local-date)) "expecting April")
    (is (= 2015 (.getYear local-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear local-date)) "expecting 2015")
    (is (= 0 (.getHours local-date)))
    (is (not= 0 (.getUTCHours local-date)) "should not be zero") ; this one is a little suprising why 14 in AEST ?
    (is (= 0 (.getMinutes local-date)))
    (is (= 0 (.getUTCMinutes local-date)))
    (is (= 0 (.getSeconds local-date)))
    (is (= 0 (.getUTCSeconds local-date)))
    (is (= 0 (.getMilliseconds local-date)))
    (is (= 0 (.getUTCMilliseconds local-date)))
    (is (not= 0 (.getTimezoneOffset local-date)) "timezone offset should not be 0")))


(deftest test:UtcDateTime_from_epoch_ms
  (let [^js/goog.date.Date utc-date (doto (js/goog.date.UtcDateTime.) (.setTime 1431216000000))] ; Midnight Sun, May 10, 2015
    (is (= 1431216000000 (.getTime utc-date)) "underlying epoch milliseconds should be kept from constructor.")
    (is (= 0 (.getWeekday utc-date)) "Expecting Sunday")
    (is (= 0 (.getUTCWeekday utc-date)) "Expecting Sunday")
    (is (= 10 (.getDate utc-date)))
    (is (= 10 (.getUTCDate utc-date)))
    (is (= 4 (.getMonth utc-date)) "expecting April")
    (is (= 4 (.getUTCMonth utc-date)) "expecting April")
    (is (= 2015 (.getYear utc-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear utc-date)) "expecting 2015")
    (is (= 0 (.getHours utc-date)))
    (is (= 0 (.getUTCHours utc-date)))
    (is (= 0 (.getMinutes utc-date)))
    (is (= 0 (.getUTCMinutes utc-date)))
    (is (= 0 (.getSeconds utc-date)))
    (is (= 0 (.getUTCSeconds utc-date)))
    (is (= 0 (.getMilliseconds utc-date)))
    (is (= 0 (.getUTCMilliseconds utc-date)))
    (is (= 0 (.getTimezoneOffset utc-date)) "timezone offset should always be 0")))


(deftest test:UtcDatetime_from_yymmdd
  (let [utc-date (js/goog.date.UtcDateTime. 2015 4 10 0 0 0 0)] ; Midnight Sun, May 10, 2015
    (is (= 0 (.getWeekday utc-date)) "Expecting Sunday")
    (is (= 0 (.getUTCWeekday utc-date)))
    (is (= 10 (.getDate utc-date)))
    (is (= 10 (.getUTCDate utc-date)))
    (is (= 4 (.getMonth utc-date)) "expecting April")
    (is (= 4 (.getUTCMonth utc-date)))
    (is (= 2015 (.getYear utc-date)) "expecting 2015")
    (is (= 2015 (.getUTCFullYear utc-date)))
    (is (= 0 (.getHours utc-date)))
    (is (= 0 (.getUTCHours utc-date)))
    (is (= 0 (.getMinutes utc-date)))
    (is (= 0 (.getUTCMinutes utc-date)))
    (is (= 0 (.getSeconds utc-date)))
    (is (= 0 (.getUTCSeconds utc-date)))
    (is (= 0 (.getMilliseconds utc-date)))
    (is (= 0 (.getUTCMilliseconds utc-date)))
    (is (= 0 (.getTimezoneOffset utc-date)) "timezone offset should always be 0")))


(deftest test:coerce-local-to-utc
  (let [local-date  (js/goog.date.DateTime.)
        ; create utc using local year, month & day midnight
        utc-date    (js/goog.date.UtcDateTime.
                      (.getYear local-date)
                      (.getMonth local-date)
                      (.getDate local-date)
                      0 0 0 0)]
    (is (not= (.getTime local-date) (.getTime utc-date)) "utc and local epoch ms should be different")
    ; reconfirm all time elements are zero
    (is (= (.getDate local-date) (.getDate utc-date)))
    (is (= 0 (.getHours utc-date)))
    (is (= 0 (.getUTCHours utc-date)))
    (is (= 0 (.getMinutes utc-date)))
    (is (= 0 (.getUTCMinutes utc-date)))
    (is (= 0 (.getSeconds utc-date)))
    (is (= 0 (.getUTCSeconds utc-date)))
    (is (= 0 (.getMilliseconds utc-date)))
    (is (= 0 (.getUTCMilliseconds utc-date)))))
