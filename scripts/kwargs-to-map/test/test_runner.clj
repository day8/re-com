#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp])

(cp/add-classpath "test")
(cp/add-classpath ".")

(require 'kwargs-to-map-test)

(def test-results
  (t/run-tests 'kwargs-to-map-test))

(def failures-and-errors
  (let [{:keys [:fail :error]} test-results]
    (+ fail error)))

(System/exit failures-and-errors)