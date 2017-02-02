(ns day8.test-runner
  (:require [cljs.test :as cljs-test :include-macros true]
            ;; Test Namespaces
            re-com.box-test
            re-com.dropdown-test
            re-com.misc-test
            re-com.selection-list-test
            re-com.time-test
            re-com.validate-test)
  (:refer-clojure :exclude (set-print-fn!)))

(defonce cp (enable-console-print!))

(defn ^:export set-print-fn! [f]
  (set! cljs.core.*print-fn* f))

(defn ^:export run-html-tests
  []
  (cljs-test/run-all-tests #".*-test"))
