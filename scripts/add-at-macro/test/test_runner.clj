#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp])

(cp/add-classpath "test")  ;; Watch the test file
(cp/add-classpath ".")   ;; Watch the main script

(require 'add-at-macro-test)

(def test-results
  (t/run-tests 'add-at-macro-test))

(def failures-and-errors
  (let [{:keys [:fail :error]} test-results]
    (+ fail error)))

(System/exit failures-and-errors)