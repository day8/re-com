(ns day8.test-runner
  (:require [cljs.test :as cljs-test :include-macros true]
            ;; Test Namespaces
            test.misc.goog-date-test
            re-com-test.box-test
            re-com-test.dropdown-test
            re-com-test.misc-test
            re-com-test.selection-list-test
            re-com-test.time-test
            re-com-test.validate-test)
  (:refer-clojure :exclude (set-print-fn!)))

(defonce cp (enable-console-print!))

(defn ^:export set-print-fn! [f]
  (set! cljs.core.*print-fn* f))

(defn ^:export run-html-tests
  []
  (cljs-test/run-all-tests #".*-test"))
